package br.com.uninter.raizesnordeste.application.dto;

import br.com.uninter.raizesnordeste.domain.enums.CanalPedidoEnum;
import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO implements Serializable {

    private Long pedidoId;
    private Long unidadeId;
    private String unidadeNome;
    private Long clienteId;
    private String clienteNome;
    private CanalPedidoEnum canalPedido;
    private StatusPedidoEnum status;
    private BigDecimal total;
    private List<ItemPedidoResponseDTO> itens;
    private LocalDateTime createdAt;
}
