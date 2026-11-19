package br.com.uninter.raizesnordeste.application.dto;

import br.com.uninter.raizesnordeste.domain.enums.StatusPagamentoEnum;
import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoResponseDTO implements Serializable {

    private String transacaoId;
    private Long pedidoId;
    private StatusPagamentoEnum statusPagamento;
    private StatusPedidoEnum statusPedido;
    private BigDecimal valor;
    private String mensagem;
    private LocalDateTime dataProcessamento;
}
