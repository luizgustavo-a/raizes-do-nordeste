package br.com.uninter.raizesnordeste.application.service;

import br.com.uninter.raizesnordeste.application.dto.*;
import br.com.uninter.raizesnordeste.domain.entity.*;
import br.com.uninter.raizesnordeste.domain.enums.CanalPedidoEnum;
import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import br.com.uninter.raizesnordeste.domain.repository.*;
import br.com.uninter.raizesnordeste.infrastructure.exception.EstoqueInsuficienteException;
import br.com.uninter.raizesnordeste.infrastructure.exception.FieldErrorDTO;
import br.com.uninter.raizesnordeste.infrastructure.exception.RegraNegocioException;
import br.com.uninter.raizesnordeste.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UnidadeRepository unidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueUnidadeRepository estoqueUnidadeRepository;
    private final PagamentoRepository pagamentoRepository;
    private final AuditoriaService auditoriaService;

    /**
     * Fluxo A - Passo 1: Criação de pedido com validação de estoque local e multicanalidade.
     */
    @Transactional
    public PedidoResponseDTO criarPedido(PedidoCreateRequestDTO dto, Usuario usuarioLogado) {
        // Validação da Unidade
        Unidade unidade = unidadeRepository.findById(dto.getUnidadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada com o ID: " + dto.getUnidadeId()));

        if (!Boolean.TRUE.equals(unidade.getAtiva())) {
            throw new RegraNegocioException("A unidade informada encontra-se inativa.");
        }

        // Determinação do cliente
        Usuario cliente = usuarioLogado;
        if (dto.getClienteId() != null) {
            cliente = usuarioRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + dto.getClienteId()));
        }

        // Verificação prévia de estoque para todos os itens (Evita atualização parcial)
        List<FieldErrorDTO> errosEstoque = new ArrayList<>();
        List<EstoqueUnidade> estoquesParaAtualizar = new ArrayList<>();
        List<ItemCalculado> itensCalculados = new ArrayList<>();

        for (int i = 0; i < dto.getItens().size(); i++) {
            ItemPedidoRequestDTO itemDto = dto.getItens().get(i);
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + itemDto.getProdutoId()));

            EstoqueUnidade estoque = estoqueUnidadeRepository
                    .findByUnidadeIdAndProdutoId(unidade.getId(), produto.getId())
                    .orElse(null);

            int disponivel = (estoque != null) ? estoque.getQuantidadeDisponivel() : 0;

            if (estoque == null || disponivel < itemDto.getQuantidade()) {
                errosEstoque.add(new FieldErrorDTO(
                        "itens[" + i + "].quantidade",
                        "Disponível: " + disponivel + " | Solicitado: " + itemDto.getQuantidade()
                ));
            } else {
                estoquesParaAtualizar.add(estoque);
                itensCalculados.add(new ItemCalculado(produto, itemDto.getQuantidade(), estoque));
            }
        }

        if (!errosEstoque.isEmpty()) {
            throw new EstoqueInsuficienteException(
                    "Não há quantidade suficiente para um ou mais itens.",
                    errosEstoque
            );
        }

        // Instanciação do Pedido
        Pedido pedido = new Pedido();
        pedido.setUnidade(unidade);
        pedido.setCliente(cliente);
        pedido.setCanalPedido(dto.getCanalPedido());
        pedido.setStatus(StatusPedidoEnum.AGUARDANDO_PAGAMENTO);

        BigDecimal total = BigDecimal.ZERO;

        for (ItemCalculado ic : itensCalculados) {
            ItemPedido item = new ItemPedido(pedido, ic.produto, ic.quantidade, ic.produto.getPreco());
            pedido.adicionarItem(item);
            total = total.add(item.getSubtotal());

            // Reserva imediata de estoque
            ic.estoque.setQuantidadeDisponivel(ic.estoque.getQuantidadeDisponivel() - ic.quantidade);
            estoqueUnidadeRepository.save(ic.estoque);
        }

        pedido.setValorTotal(total);
        Pedido salvo = pedidoRepository.save(pedido);

        // Registro da intenção de pagamento desacoplado
        Pagamento pagamento = new Pagamento(salvo, dto.getFormaPagamento(), total);
        pagamentoRepository.save(pagamento);

        // Registro de Auditoria da Ação Sensível
        auditoriaService.registrar(
                "CRIACAO_PEDIDO",
                "Pedido",
                salvo.getId(),
                cliente.getEmail(),
                String.format("Pedido criado via canal %s na unidade %s. Total: R$ %.2f",
                        salvo.getCanalPedido(), unidade.getNome(), total)
        );

        return toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> listarPedidos(CanalPedidoEnum canalPedido,
                                                 StatusPedidoEnum status,
                                                 Long unidadeId,
                                                 Pageable pageable) {
        return pedidoRepository.findComFiltros(canalPedido, status, unidadeId, pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findByIdWithItens(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + id));
        return toResponseDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Long id, StatusAtualizacaoDTO dto, Usuario usuarioResponsavel) {
        Pedido pedido = pedidoRepository.findByIdWithItens(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + id));

        StatusPedidoEnum statusAnterior = pedido.getStatus();
        StatusPedidoEnum novoStatus = dto.getStatus();

        if (statusAnterior == StatusPedidoEnum.CANCELADO || statusAnterior == StatusPedidoEnum.ENTREGUE) {
            throw new RegraNegocioException("Não é permitido alterar status de um pedido já " + statusAnterior.name());
        }

        pedido.setStatus(novoStatus);

        // Se o pedido for cancelado, repõe o estoque reservado
        if (novoStatus == StatusPedidoEnum.CANCELADO) {
            for (ItemPedido item : pedido.getItens()) {
                estoqueUnidadeRepository.findByUnidadeIdAndProdutoId(pedido.getUnidade().getId(), item.getProduto().getId())
                        .ifPresent(est -> {
                            est.setQuantidadeDisponivel(est.getQuantidadeDisponivel() + item.getQuantidade());
                            estoqueUnidadeRepository.save(est);
                        });
            }
        }

        Pedido atualizado = pedidoRepository.save(pedido);

        // Auditoria de Operação Sensível
        auditoriaService.registrar(
                "ATUALIZACAO_STATUS_PEDIDO",
                "Pedido",
                atualizado.getId(),
                usuarioResponsavel != null ? usuarioResponsavel.getEmail() : "SISTEMA",
                String.format("Status alterado de %s para %s. Motivo: %s",
                        statusAnterior, novoStatus, dto.getJustificativa() != null ? dto.getJustificativa() : "Operação de rotina")
        );

        return toResponseDTO(atualizado);
    }

    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        List<ItemPedidoResponseDTO> itensDTO = pedido.getItens().stream()
                .map(i -> new ItemPedidoResponseDTO(
                        i.getProduto().getId(),
                        i.getProduto().getNome(),
                        i.getQuantidade(),
                        i.getPrecoUnitario(),
                        i.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getUnidade().getId(),
                pedido.getUnidade().getNome(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNome(),
                pedido.getCanalPedido(),
                pedido.getStatus(),
                pedido.getValorTotal(),
                itensDTO,
                pedido.getDataCriacao()
        );
    }

    private static class ItemCalculado {
        final Produto produto;
        final int quantidade;
        final EstoqueUnidade estoque;

        ItemCalculado(Produto produto, int quantidade, EstoqueUnidade estoque) {
            this.produto = produto;
            this.quantidade = quantidade;
            this.estoque = estoque;
        }
    }
}
