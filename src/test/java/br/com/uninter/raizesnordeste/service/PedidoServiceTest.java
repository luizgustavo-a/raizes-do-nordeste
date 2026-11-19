package br.com.uninter.raizesnordeste.service;

import br.com.uninter.raizesnordeste.application.dto.ItemPedidoRequestDTO;
import br.com.uninter.raizesnordeste.application.dto.PedidoCreateRequestDTO;
import br.com.uninter.raizesnordeste.application.dto.PedidoResponseDTO;
import br.com.uninter.raizesnordeste.application.service.AuditoriaService;
import br.com.uninter.raizesnordeste.application.service.PedidoService;
import br.com.uninter.raizesnordeste.domain.entity.*;
import br.com.uninter.raizesnordeste.domain.enums.CanalPedidoEnum;
import br.com.uninter.raizesnordeste.domain.enums.PerfilEnum;
import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import br.com.uninter.raizesnordeste.domain.repository.*;
import br.com.uninter.raizesnordeste.infrastructure.exception.EstoqueInsuficienteException;
import br.com.uninter.raizesnordeste.infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários das regras de negócio do fluxo de pedidos.
 */
@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UnidadeRepository unidadeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private EstoqueUnidadeRepository estoqueUnidadeRepository;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private PedidoService pedidoService;

    private Unidade unidadeRecife;
    private Produto cuscuz;
    private EstoqueUnidade estoqueCuscuz;
    private Usuario clienteAna;

    @BeforeEach
    void setUp() {
        unidadeRecife = new Unidade(1L, "Matriz Recife - Boa Viagem", "Av. Conselheiro Aguiar", "Recife", "PE", true);
        cuscuz = new Produto(1L, "Cuscuz com Carne de Sol", "Cuscuz tradicional", new BigDecimal("28.50"), "PRATO_PRINCIPAL", true);
        estoqueCuscuz = new EstoqueUnidade(1L, unidadeRecife, cuscuz, 10);
        clienteAna = new Usuario(4L, "Ana Silva", "ana.silva@email.com", "hash", PerfilEnum.CLIENTE, "81988887777", true);
    }

    @Test
    @DisplayName("T06 - Deve criar pedido com sucesso quando estoque for suficiente e canalPedido informado")
    void deveCriarPedidoComSucesso() {
        // Cenário: Pedido via TOTEM de 2 cuscuz
        PedidoCreateRequestDTO dto = new PedidoCreateRequestDTO();
        dto.setUnidadeId(1L);
        dto.setCanalPedido(CanalPedidoEnum.TOTEM);
        dto.setItens(List.of(new ItemPedidoRequestDTO(1L, 2)));
        dto.setFormaPagamento("PIX");

        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidadeRecife));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(cuscuz));
        when(estoqueUnidadeRepository.findByUnidadeIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(estoqueCuscuz));

        Pedido pedidoSimulado = new Pedido();
        pedidoSimulado.setId(1001L);
        pedidoSimulado.setUnidade(unidadeRecife);
        pedidoSimulado.setCliente(clienteAna);
        pedidoSimulado.setCanalPedido(CanalPedidoEnum.TOTEM);
        pedidoSimulado.setStatus(StatusPedidoEnum.AGUARDANDO_PAGAMENTO);
        pedidoSimulado.setValorTotal(new BigDecimal("57.00"));
        pedidoSimulado.adicionarItem(new ItemPedido(pedidoSimulado, cuscuz, 2, new BigDecimal("28.50")));

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSimulado);

        // Execução
        PedidoResponseDTO response = pedidoService.criarPedido(dto, clienteAna);

        // Verificações
        assertNotNull(response, "O response não pode ser nulo");
        assertEquals(1001L, response.getPedidoId());
        assertEquals(CanalPedidoEnum.TOTEM, response.getCanalPedido());
        assertEquals(StatusPedidoEnum.AGUARDANDO_PAGAMENTO, response.getStatus());
        assertEquals(new BigDecimal("57.00"), response.getTotal());

        // Verifica se a reserva no estoque ocorreu (10 - 2 = 8)
        assertEquals(8, estoqueCuscuz.getQuantidadeDisponivel());
        verify(estoqueUnidadeRepository, times(1)).save(estoqueCuscuz);
        verify(auditoriaService, times(1)).registrar(eq("CRIACAO_PEDIDO"), eq("Pedido"), any(), any(), any());
    }

    @Test
    @DisplayName("T09 - Deve lançar EstoqueInsuficienteException (HTTP 409) quando a quantidade solicitada exceder o saldo")
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        // Cenário: Solicita 15 unidades de cuscuz, mas a loja só tem 10
        PedidoCreateRequestDTO dto = new PedidoCreateRequestDTO();
        dto.setUnidadeId(1L);
        dto.setCanalPedido(CanalPedidoEnum.APP);
        dto.setItens(List.of(new ItemPedidoRequestDTO(1L, 15)));

        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidadeRecife));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(cuscuz));
        when(estoqueUnidadeRepository.findByUnidadeIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(estoqueCuscuz));

        // Execução e Asserção
        EstoqueInsuficienteException ex = assertThrows(EstoqueInsuficienteException.class, () -> {
            pedidoService.criarPedido(dto, clienteAna);
        });

        assertTrue(ex.getMessage().contains("Não há quantidade suficiente"));
        assertFalse(ex.getDetails().isEmpty(), "Detalhes do erro devem conter o campo afetado");
        assertEquals("itens[0].quantidade", ex.getDetails().get(0).getField());

        // Garante que nenhum pedido foi gravado
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("T08 - Deve lançar ResourceNotFoundException (HTTP 404) quando a unidade informada não existir")
    void deveLancarExcecaoQuandoUnidadeInexistente() {
        PedidoCreateRequestDTO dto = new PedidoCreateRequestDTO();
        dto.setUnidadeId(9999L);
        dto.setCanalPedido(CanalPedidoEnum.BALCAO);
        dto.setItens(List.of(new ItemPedidoRequestDTO(1L, 1)));

        when(unidadeRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            pedidoService.criarPedido(dto, clienteAna);
        });
    }
}
