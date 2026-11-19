package br.com.uninter.raizesnordeste.api.controller;

import br.com.uninter.raizesnordeste.application.dto.PagamentoResponseDTO;
import br.com.uninter.raizesnordeste.application.dto.PagamentoSimulacaoDTO;
import br.com.uninter.raizesnordeste.application.service.PagamentoService;
import br.com.uninter.raizesnordeste.domain.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos Desacoplados (Gateway Mock)",
        description = "Integração desacoplada com simulação de autorização e recusa de pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping("/simular")
    @Operation(summary = "Simular processamento de pagamento mock",
            description = "Processa a resposta do gateway externo. Se simularAprovado=true, aprova e move para cozinha; se false, recusa e estorna estoque.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Processamento do pagamento concluído (Aprovado ou Recusado)"),
            @ApiResponse(responseCode = "400", description = "Pedido já pago ou cancelado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados da solicitação inválidos")
    })
    public ResponseEntity<PagamentoResponseDTO> simularPagamento(
            @Valid @RequestBody PagamentoSimulacaoDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        String email = usuarioLogado != null ? usuarioLogado.getEmail() : "CLIENTE_SISTEMA";
        PagamentoResponseDTO response = pagamentoService.processarPagamentoMock(dto, email);
        return ResponseEntity.ok(response);
    }
}
