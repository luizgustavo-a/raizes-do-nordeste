package br.com.uninter.raizesnordeste.api.controller;

import br.com.uninter.raizesnordeste.application.dto.CadastroUsuarioDTO;
import br.com.uninter.raizesnordeste.application.dto.LoginRequestDTO;
import br.com.uninter.raizesnordeste.application.dto.LoginResponseDTO;
import br.com.uninter.raizesnordeste.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação e Usuários", description = "Endpoints para login e cadastro com conformidade LGPD")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Realiza a autenticação via credenciais e retorna o token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso"),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos"),
            @ApiResponse(responseCode = "422", description = "Campos inválidos ou mal formatados")
    })
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cadastro")
    @Operation(summary = "Cadastrar novo cliente com termo LGPD", description = "Cadastra cliente e registra o consentimento de dados.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "E-mail já existente ou consentimento recusado"),
            @ApiResponse(responseCode = "422", description = "Validação de dados falhou")
    })
    public ResponseEntity<LoginResponseDTO> cadastrar(@Valid @RequestBody CadastroUsuarioDTO dto, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        LoginResponseDTO response = authService.cadastrar(dto, ip);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
