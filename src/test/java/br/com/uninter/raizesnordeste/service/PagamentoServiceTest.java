package br.com.uninter.raizesnordeste.service;

import br.com.uninter.raizesnordeste.application.dto.PagamentoResponseDTO;
import br.com.uninter.raizesnordeste.application.dto.PagamentoSimulacaoDTO;
import br.com.uninter.raizesnordeste.application.service.AuditoriaService;
import br.com.uninter.raizesnordeste.application.service.PagamentoService;
import br.com.uninter.raizesnordeste.domain.entity.*;
import br.com.uninter.raizesnordeste.domain.enums.CanalPedidoEnum;
import br.com.uninter.raizesnordeste.domain.enums.PerfilEnum;
import br.com.uninter.raizesnordeste.domain.enums.StatusPagamentoEnum;
import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import br.com.uninter.raizesnordeste.domain.repository.EstoqueUnidadeRepository;
import br.com.uninter.raizesnordeste.domain.repository.PagamentoRepository;
import br.com.uninter.raizesnordeste.domain.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private EstoqueUnidadeRepository estoqueUnidadeRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Pedido pedido;
    private Unidade unidade;
    private Produto produto;
    private EstoqueUnidade estoque;

    @BeforeEach
    void setUp() {
        unidade = new Unidade(1L, "Recife Derby", "Rua Benfica", "Recife", "PE", true);
        produto = new Produto(1L, "Tapioca Rendada", "Tapioca com queijo", new BigDecimal("19.90"), "LANCHE", true);
        estoque = new EstoqueUnidade(1L, unidade, produto, 8); // Estoque já havia baixado 2 unidades

        Usuario cliente = new Usuario(4L, "Ana Silva", "ana@email.com", "senha", PerfilEnum.CLIENTE, "819", true);

        pedido = new Pedido(100L, unidade, cliente, CanalPedidoEnum.TOTEM, StatusPedidoEnum.AGUARDANDO_PAGAMENTO, new BigDecimal("39.80"));
        pedido.adicionarItem(new ItemPedido(pedido, produto, 2, new BigDecimal("19.90")));
    }

    @Test
    @DisplayName("T11 - Deve aprovar pagamento mock e atualizar status do pedido para EM_PREPARACAO")
    void deveAprovarPagamentoMock() {
        PagamentoSimulacaoDTO dto = new PagamentoSimulacaoDTO(100L, "PIX", true);

        when(pedidoRepository.findByIdWithItens(100L)).thenReturn(Optional.of(pedido));
        when(pagamentoRepository.findByPedidoId(100L)).thenReturn(Optional.empty());

        PagamentoResponseDTO response = pagamentoService.processarPagamentoMock(dto, "ana@email.com");

        assertNotNull(response);
        assertEquals(StatusPagamentoEnum.APROVADO, response.getStatusPagamento());
        assertEquals(StatusPedidoEnum.EM_PREPARACAO, response.getStatusPedido());
        assertTrue(response.getTransacaoId().startsWith("MOCK-"));

        verify(pedidoRepository, times(1)).save(pedido);
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("T11.1 - Deve recusar pagamento mock e devolver estoque reservado automaticamente")
    void deveRecusarPagamentoMockEEstornarEstoque() {
        PagamentoSimulacaoDTO dto = new PagamentoSimulacaoDTO(100L, "CARTAO_CREDITO", false);

        when(pedidoRepository.findByIdWithItens(100L)).thenReturn(Optional.of(pedido));
        when(pagamentoRepository.findByPedidoId(100L)).thenReturn(Optional.empty());
        when(estoqueUnidadeRepository.findByUnidadeIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(estoque));

        PagamentoResponseDTO response = pagamentoService.processarPagamentoMock(dto, "ana@email.com");

        assertNotNull(response);
        assertEquals(StatusPagamentoEnum.RECUSADO, response.getStatusPagamento());
        assertEquals(StatusPedidoEnum.PAGAMENTO_RECUSADO, response.getStatusPedido());

        // Estoque anterior era 8 + 2 devolvidos = 10
        assertEquals(10, estoque.getQuantidadeDisponivel());
        verify(estoqueUnidadeRepository, times(1)).save(estoque);
    }
}
