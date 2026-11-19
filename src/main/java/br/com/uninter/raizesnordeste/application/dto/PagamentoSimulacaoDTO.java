package br.com.uninter.raizesnordeste.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO para simulação do processamento de pagamento mock (sucesso ou falha).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoSimulacaoDTO implements Serializable {

    @NotNull(message = "O ID do pedido é obrigatório.")
    private Long pedidoId;

    @NotBlank(message = "A forma de pagamento é obrigatória.")
    private String formaPagamento;

    @NotNull(message = "O parâmetro simularAprovado (true ou false) é obrigatório.")
    private Boolean simularAprovado;
}
