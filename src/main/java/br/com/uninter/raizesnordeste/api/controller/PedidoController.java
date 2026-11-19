package br.com.uninter.raizesnordeste.api.controller;

import br.com.uninter.raizesnordeste.application.dto.PedidoCreateRequestDTO;
import br.com.uninter.raizesnordeste.application.dto.PedidoResponseDTO;
import br.com.uninter.raizesnordeste.application.dto.StatusAtualizacaoDTO;
import br.com.uninter.raizesnordeste.application.service.PedidoService;
import br.com.uninter.raizesnordeste.domain.entity.Usuario;
import br.com.uninter.raizesnordeste.domain.enums.CanalPedidoEnum;
import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos (Fluxo Crítico)", description = "Endpoints de criação multicanal, consulta e ciclo de vida do pedido")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Criar novo pedido (Fluxo Crítico)",
            description = "Valida estoque na unidade, exige o campo canalPedido (APP, TOTEM, BALCAO, PICKUP, WEB) e reserva itens.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso aguardando pagamento"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou canalPedido ausente/inválido"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou expirado"),
            @ApiResponse(responseCode = "404", description = "Unidade ou Produto inexistente"),
            @ApiResponse(responseCode = "409", description = "Conflito: Estoque insuficiente na filial selecionada"),
            @ApiResponse(responseCode = "422", description = "Campos obrigatórios não atendidos")
    })
    public ResponseEntity<PedidoResponseDTO> criarPedido(
            @Valid @RequestBody PedidoCreateRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        PedidoResponseDTO response = pedidoService.criarPedido(dto, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar pedidos com filtros (Multicanalidade e Status)",
            description = "Permite filtrar pedidos por canalPedido (ex: TOTEM, APP), status e filial com paginação.")
    public ResponseEntity<Page<PedidoResponseDTO>> listarPedidos(
            @Parameter(description = "Canal de origem (APP, TOTEM, BALCAO, PICKUP, WEB)")
            @RequestParam(required = false) CanalPedidoEnum canalPedido,
            @Parameter(description = "Status do pedido")
            @RequestParam(required = false) StatusPedidoEnum status,
            @Parameter(description = "ID da filial")
            @RequestParam(required = false) Long unidadeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<PedidoResponseDTO> pedidos = pedidoService.listarPedidos(canalPedido, status, unidadeId, pageable);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID com itens", description = "Retorna os detalhes completos do pedido e itens solicitados.")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ATENDENTE', 'GERENTE', 'ADMIN')")
    @Operation(summary = "Atualizar status do pedido (Cozinha/Balcão)",
            description = "Transições de estado: EM_PREPARACAO -> PRONTO -> ENTREGUE / CANCELADO. Requer permissão operacional.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Transição de status inválida"),
            @ApiResponse(responseCode = "403", description = "Perfil sem permissão para atualizar status"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusAtualizacaoDTO dto,
            @AuthenticationPrincipal Usuario usuarioResponsavel) {
        PedidoResponseDTO response = pedidoService.atualizarStatus(id, dto, usuarioResponsavel);
        return ResponseEntity.ok(response);
    }
}
