package br.com.uninter.raizesnordeste.api.controller;

import br.com.uninter.raizesnordeste.application.dto.AuditoriaResponseDTO;
import br.com.uninter.raizesnordeste.domain.repository.AuditoriaLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auditoria")
@RequiredArgsConstructor
@Tag(name = "Auditoria de Operações Sensíveis", description = "Consulta do registro de auditoria. Restrito a Gerentes e Administradores.")
public class AuditoriaController {

    private final AuditoriaLogRepository auditoriaLogRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @Operation(summary = "Listar registros de auditoria",
            description = "Retorna histórico de operações sensíveis (criação de pedido, cancelamento, alteração de status).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de auditoria retornada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: Perfil CLIENTE ou ATENDENTE não possui permissão")
    })
    public ResponseEntity<Page<AuditoriaResponseDTO>> listarLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        Page<AuditoriaResponseDTO> logs = auditoriaLogRepository.findAllByOrderByDataHoraDesc(pageable)
                .map(l -> new AuditoriaResponseDTO(
                        l.getId(),
                        l.getAcao(),
                        l.getEntidade(),
                        l.getEntidadeId(),
                        l.getUsuarioResponsavel(),
                        l.getDetalhes(),
                        l.getDataHora()
                ));

        return ResponseEntity.ok(logs);
    }
}
