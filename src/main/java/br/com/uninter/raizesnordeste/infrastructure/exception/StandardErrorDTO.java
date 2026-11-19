package br.com.uninter.raizesnordeste.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO padrão para respostas de erro da API REST.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardErrorDTO implements Serializable {

    private String error;
    private String message;
    private List<FieldErrorDTO> details = new ArrayList<>();
    private String timestamp;
    private String path;
    private String requestId;

    public StandardErrorDTO() {
        this.timestamp = Instant.now().toString();
    }

    public StandardErrorDTO(String error, String message, String path) {
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = Instant.now().toString();
    }

    public StandardErrorDTO(String error, String message, List<FieldErrorDTO> details, String path) {
        this.error = error;
        this.message = message;
        this.details = details != null ? details : new ArrayList<>();
        this.path = path;
        this.timestamp = Instant.now().toString();
    }
}
