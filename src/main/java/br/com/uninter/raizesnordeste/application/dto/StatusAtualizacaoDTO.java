package br.com.uninter.raizesnordeste.application.dto;

import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class StatusAtualizacaoDTO implements Serializable {

    @NotNull(message = "O novo status do pedido é obrigatório.")
    private StatusPedidoEnum status;

    private String justificativa;

    public StatusAtualizacaoDTO() {
    }

    public StatusAtualizacaoDTO(StatusPedidoEnum status, String justificativa) {
        this.status = status;
        this.justificativa = justificativa;
    }

    public StatusPedidoEnum getStatus() {
        return status;
    }

    public void setStatus(StatusPedidoEnum status) {
        this.status = status;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}
