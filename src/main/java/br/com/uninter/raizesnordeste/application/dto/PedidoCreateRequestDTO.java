package br.com.uninter.raizesnordeste.application.dto;

import br.com.uninter.raizesnordeste.domain.enums.CanalPedidoEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * DTO de entrada para criação de pedidos multicanal.
 */
@Data
@NoArgsConstructor
public class PedidoCreateRequestDTO implements Serializable {

    @NotNull(message = "O campo 'canalPedido' é obrigatório. Valores aceitos: APP, TOTEM, BALCAO, PICKUP, WEB.")
    private CanalPedidoEnum canalPedido;

    @NotNull(message = "O ID da unidade é obrigatório.")
    private Long unidadeId;

    private Long clienteId; // Opcional se identificado pelo Token JWT

    @NotEmpty(message = "O pedido deve conter pelo menos 1 item.")
    @Valid
    private List<ItemPedidoRequestDTO> itens;

    private String formaPagamento = "MOCK";
}
