package br.com.uninter.raizesnordeste.controller;

import br.com.uninter.raizesnordeste.api.controller.PedidoController;
import br.com.uninter.raizesnordeste.application.dto.ItemPedidoRequestDTO;
import br.com.uninter.raizesnordeste.application.dto.PedidoCreateRequestDTO;
import br.com.uninter.raizesnordeste.application.dto.PedidoResponseDTO;
import br.com.uninter.raizesnordeste.application.service.PedidoService;
import br.com.uninter.raizesnordeste.domain.enums.CanalPedidoEnum;
import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import br.com.uninter.raizesnordeste.domain.repository.UsuarioRepository;
import br.com.uninter.raizesnordeste.infrastructure.exception.EstoqueInsuficienteException;
import br.com.uninter.raizesnordeste.infrastructure.exception.FieldErrorDTO;
import br.com.uninter.raizesnordeste.infrastructure.security.JwtAuthenticationFilter;
import br.com.uninter.raizesnordeste.infrastructure.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes da camada Web (Controller) com Spring MockMvc e JUnit 5.
 * Foco na validação de contratos JSON, status codes HTTP e tratamento global de exceções.
 */
@WebMvcTest(PedidoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PedidoService pedidoService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("T06 - Deve retornar HTTP 201 quando o pedido for criado com sucesso via TOTEM")
    void deveRetornar201AoCriarPedidoComSucesso() throws Exception {
        PedidoCreateRequestDTO dto = new PedidoCreateRequestDTO();
        dto.setUnidadeId(1L);
        dto.setCanalPedido(CanalPedidoEnum.TOTEM);
        dto.setItens(List.of(new ItemPedidoRequestDTO(1L, 2)));
        dto.setFormaPagamento("PIX");

        PedidoResponseDTO responseDTO = new PedidoResponseDTO(
                1001L,
                1L,
                "Matriz Recife - Boa Viagem",
                4L,
                "Ana Silva",
                CanalPedidoEnum.TOTEM,
                StatusPedidoEnum.AGUARDANDO_PAGAMENTO,
                new BigDecimal("57.00"),
                List.of(),
                LocalDateTime.now()
        );

        when(pedidoService.criarPedido(any(), any())).thenReturn(responseDTO);

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pedidoId").value(1001))
                .andExpect(jsonPath("$.canalPedido").value("TOTEM"))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO"))
                .andExpect(jsonPath("$.total").value(57.00));
    }

    @Test
    @DisplayName("T07 - Deve retornar HTTP 422 quando o campo obrigatório canalPedido não for informado")
    void deveRetornar422QuandoCanalPedidoForNulo() throws Exception {
        PedidoCreateRequestDTO dto = new PedidoCreateRequestDTO();
        dto.setUnidadeId(1L);
        dto.setCanalPedido(null);
        dto.setItens(List.of(new ItemPedidoRequestDTO(1L, 1)));

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("DADOS_INVALIDOS"));
    }

    @Test
    @DisplayName("T07.1 - Deve retornar HTTP 422 quando a quantidade do item for negativa ou zero")
    void deveRetornar422QuandoQuantidadeForNegativa() throws Exception {
        PedidoCreateRequestDTO dto = new PedidoCreateRequestDTO();
        dto.setUnidadeId(1L);
        dto.setCanalPedido(CanalPedidoEnum.APP);
        dto.setItens(List.of(new ItemPedidoRequestDTO(1L, -2)));

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("DADOS_INVALIDOS"))
                .andExpect(jsonPath("$.details[0].field").value("itens[0].quantidade"));
    }

    @Test
    @DisplayName("T09 - Deve retornar HTTP 409 quando o PedidoService indicar estoque insuficiente")
    void deveRetornar409QuandoEstoqueInsuficiente() throws Exception {
        PedidoCreateRequestDTO dto = new PedidoCreateRequestDTO();
        dto.setUnidadeId(2L);
        dto.setCanalPedido(CanalPedidoEnum.APP);
        dto.setItens(List.of(new ItemPedidoRequestDTO(1L, 999)));

        when(pedidoService.criarPedido(any(), any())).thenThrow(new EstoqueInsuficienteException(
                "Não há quantidade suficiente para um ou mais itens.",
                List.of(new FieldErrorDTO("itens[0].quantidade", "Disponível: 2 | Solicitado: 999"))
        ));

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("ESTOQUE_INSUFICIENTE"))
                .andExpect(jsonPath("$.details[0].field").value("itens[0].quantidade"));
    }
}
