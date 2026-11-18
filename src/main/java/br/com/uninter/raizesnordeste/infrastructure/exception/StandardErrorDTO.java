package br.com.uninter.raizesnordeste.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO padrão para respostas de erro da API REST.
 */
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FieldErrorDTO> getDetails() {
        return details;
    }

    public void setDetails(List<FieldErrorDTO> details) {
        this.details = details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
