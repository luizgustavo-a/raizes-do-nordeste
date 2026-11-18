package br.com.uninter.raizesnordeste.infrastructure.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Lançada quando não há estoque suficiente para atender a um ou mais itens do pedido.
 * Mapeada para HTTP 409 CONFLICT.
 */
public class EstoqueInsuficienteException extends RuntimeException {

    private final List<FieldErrorDTO> details = new ArrayList<>();

    public EstoqueInsuficienteException(String message) {
        super(message);
    }

    public EstoqueInsuficienteException(String message, String field, String issue) {
        super(message);
        this.details.add(new FieldErrorDTO(field, issue));
    }

    public EstoqueInsuficienteException(String message, List<FieldErrorDTO> details) {
        super(message);
        if (details != null) {
            this.details.addAll(details);
        }
    }

    public List<FieldErrorDTO> getDetails() {
        return details;
    }
}
