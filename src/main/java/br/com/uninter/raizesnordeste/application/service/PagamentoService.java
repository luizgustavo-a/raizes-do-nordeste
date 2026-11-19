package br.com.uninter.raizesnordeste.application.service;

import br.com.uninter.raizesnordeste.application.dto.PagamentoResponseDTO;
import br.com.uninter.raizesnordeste.application.dto.PagamentoSimulacaoDTO;
import br.com.uninter.raizesnordeste.domain.entity.ItemPedido;
import br.com.uninter.raizesnordeste.domain.entity.Pagamento;
import br.com.uninter.raizesnordeste.domain.entity.Pedido;
import br.com.uninter.raizesnordeste.domain.enums.StatusPagamentoEnum;
import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import br.com.uninter.raizesnordeste.domain.repository.EstoqueUnidadeRepository;
import br.com.uninter.raizesnordeste.domain.repository.PagamentoRepository;
import br.com.uninter.raizesnordeste.domain.repository.PedidoRepository;
import br.com.uninter.raizesnordeste.infrastructure.exception.RegraNegocioException;
import br.com.uninter.raizesnordeste.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Serviço responsável por simular a integração desacoplada com gateway externo de pagamento.
 */
@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final EstoqueUnidadeRepository estoqueUnidadeRepository;
    private final AuditoriaService auditoriaService;

    @Transactional
    public PagamentoResponseDTO processarPagamentoMock(PagamentoSimulacaoDTO dto, String usuarioEmail) {
        Pedido pedido = pedidoRepository.findByIdWithItens(dto.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + dto.getPedidoId()));

        if (pedido.getStatus() != StatusPedidoEnum.AGUARDANDO_PAGAMENTO) {
            throw new RegraNegocioException("O pedido #" + pedido.getId() + " não está aguardando pagamento. Status atual: " + pedido.getStatus());
        }

        Pagamento pagamento = pagamentoRepository.findByPedidoId(pedido.getId())
                .orElseGet(() -> new Pagamento(pedido, dto.getFormaPagamento(), pedido.getValorTotal()));

        pagamento.setFormaPagamento(dto.getFormaPagamento());
        pagamento.setDataProcessamento(LocalDateTime.now());
        pagamento.setTransacaoExternaId("MOCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        if (Boolean.TRUE.equals(dto.getSimularAprovado())) {
            // Cenário Positivo: Pagamento Aprovado
            pagamento.setStatus(StatusPagamentoEnum.APROVADO);
            pagamento.setMensagemRetorno("Transação autorizada com sucesso pelo Gateway Externo Mock.");
            pedido.setStatus(StatusPedidoEnum.EM_PREPARACAO);

            auditoriaService.registrar(
                    "PAGAMENTO_APROVADO",
                    "Pagamento",
                    pagamento.getId(),
                    usuarioEmail != null ? usuarioEmail : pedido.getCliente().getEmail(),
                    String.format("Pagamento de R$ %.2f aprovado para o pedido #%d. Pedido enviado para cozinha.",
                            pedido.getValorTotal(), pedido.getId())
            );
        } else {
            // Cenário Negativo: Pagamento Recusado
            pagamento.setStatus(StatusPagamentoEnum.RECUSADO);
            pagamento.setMensagemRetorno("Transação recusada pela instituição financeira (Saldo insuficiente / Bloqueio).");
            pedido.setStatus(StatusPedidoEnum.PAGAMENTO_RECUSADO);

            // Devolução imediata do estoque que havia sido reservado
            for (ItemPedido item : pedido.getItens()) {
                estoqueUnidadeRepository.findByUnidadeIdAndProdutoId(pedido.getUnidade().getId(), item.getProduto().getId())
                        .ifPresent(est -> {
                            est.setQuantidadeDisponivel(est.getQuantidadeDisponivel() + item.getQuantidade());
                            estoqueUnidadeRepository.save(est);
                        });
            }

            auditoriaService.registrar(
                    "PAGAMENTO_RECUSADO",
                    "Pagamento",
                    pagamento.getId(),
                    usuarioEmail != null ? usuarioEmail : pedido.getCliente().getEmail(),
                    String.format("Pagamento recusado para o pedido #%d. Estoque liberado automaticamente.", pedido.getId())
            );
        }

        pagamentoRepository.save(pagamento);
        pedidoRepository.save(pedido);

        return new PagamentoResponseDTO(
                pagamento.getTransacaoExternaId(),
                pedido.getId(),
                pagamento.getStatus(),
                pedido.getStatus(),
                pagamento.getValor(),
                pagamento.getMensagemRetorno(),
                pagamento.getDataProcessamento()
        );
    }
}
