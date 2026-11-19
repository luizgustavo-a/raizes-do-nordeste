package br.com.uninter.raizesnordeste.application.dto;

import br.com.uninter.raizesnordeste.domain.enums.StatusPagamentoEnum;
import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoResponseDTO implements Serializable {

    private String transacaoId;
    private Long pedidoId;
    private StatusPagamentoEnum statusPagamento;
    private StatusPedidoEnum statusPedido;
    private BigDecimal valor;
    private String mensagem;
    private LocalDateTime dataProcessamento;

    public PagamentoResponseDTO() {
    }

    public PagamentoResponseDTO(String transacaoId, Long pedidoId, StatusPagamentoEnum statusPagamento,
                                StatusPedidoEnum statusPedido, BigDecimal valor, String mensagem,
                                LocalDateTime dataProcessamento) {
        this.transacaoId = transacaoId;
        this.pedidoId = pedidoId;
        this.statusPagamento = statusPagamento;
        this.statusPedido = statusPedido;
        this.valor = valor;
        this.mensagem = mensagem;
        this.dataProcessamento = dataProcessamento;
    }

    public String getTransacaoId() {
        return transacaoId;
    }

    public void setTransacaoId(String transacaoId) {
        this.transacaoId = transacaoId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public StatusPagamentoEnum getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamentoEnum statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public StatusPedidoEnum getStatusPedido() {
        return statusPedido;
    }

    public void setStatusPedido(StatusPedidoEnum statusPedido) {
        this.statusPedido = statusPedido;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }

    public void setDataProcessamento(LocalDateTime dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }
}
