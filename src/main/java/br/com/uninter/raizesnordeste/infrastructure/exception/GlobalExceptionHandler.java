package br.com.uninter.raizesnordeste.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler global para padronizar as respostas de erro da API em JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardErrorDTO> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                "RECURSO_NAO_ENCONTRADO",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<StandardErrorDTO> handleEstoqueInsuficiente(EstoqueInsuficienteException ex, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                "ESTOQUE_INSUFICIENTE",
                ex.getMessage(),
                ex.getDetails(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<StandardErrorDTO> handleRegraNegocio(RegraNegocioException ex, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                "REGRA_NEGOCIO_VIOLADA",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardErrorDTO> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldErrorDTO> details = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            details.add(new FieldErrorDTO(fe.getField(), fe.getDefaultMessage()));
        }

        StandardErrorDTO error = new StandardErrorDTO(
                "DADOS_INVALIDOS",
                "Erro de validação nos campos da requisição.",
                details,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardErrorDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String msg = "Corpo da requisição inválido ou mal formatado.";
        if (ex.getMessage() != null && ex.getMessage().contains("CanalPedidoEnum")) {
            msg = "Canal de pedido inválido. Valores aceitos: APP, TOTEM, BALCAO, PICKUP, WEB.";
        }

        StandardErrorDTO error = new StandardErrorDTO(
                "REQUISICAO_INVALIDA",
                msg,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardErrorDTO> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                "CREDENCIAIS_INVALIDAS",
                "E-mail ou senha inválidos.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardErrorDTO> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                "ACESSO_NEGADO",
                "Você não possui permissão para acessar este recurso.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardErrorDTO> handleGenericException(Exception ex, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                "ERRO_INTERNO",
                "Ocorreu um erro interno no servidor: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
