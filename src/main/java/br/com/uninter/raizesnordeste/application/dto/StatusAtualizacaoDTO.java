package br.com.uninter.raizesnordeste.application.dto;

import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusAtualizacaoDTO implements Serializable {

    @NotNull(message = "O novo status do pedido é obrigatório.")
    private StatusPedidoEnum status;

    private String justificativa;
}
