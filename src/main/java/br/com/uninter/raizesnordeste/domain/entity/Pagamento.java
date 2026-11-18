package br.com.uninter.raizesnordeste.domain.entity;

import br.com.uninter.raizesnordeste.domain.enums.StatusPagamentoEnum;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade de registro de pagamentos processados por serviço externo simulado.
 */
@Entity
@Table(name = "tb_pagamento")
public class Pagamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @Column(name = "transacao_externa_id", length = 100)
    private String transacaoExternaId;

    @Column(name = "forma_pagamento", nullable = false, length = 50)
    private String formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPagamentoEnum status = StatusPagamentoEnum.PENDENTE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_processamento")
    private LocalDateTime dataProcessamento;

    @Column(name = "mensagem_retorno", length = 255)
    private String mensagemRetorno;

    public Pagamento() {
    }

    public Pagamento(Pedido pedido, String formaPagamento, BigDecimal valor) {
        this.pedido = pedido;
        this.formaPagamento = formaPagamento;
        this.valor = valor;
        this.status = StatusPagamentoEnum.PENDENTE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getTransacaoExternaId() {
        return transacaoExternaId;
    }

    public void setTransacaoExternaId(String transacaoExternaId) {
        this.transacaoExternaId = transacaoExternaId;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public StatusPagamentoEnum getStatus() {
        return status;
    }

    public void setStatus(StatusPagamentoEnum status) {
        this.status = status;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }

    public void setDataProcessamento(LocalDateTime dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    public String getMensagemRetorno() {
        return mensagemRetorno;
    }

    public void setMensagemRetorno(String mensagemRetorno) {
        this.mensagemRetorno = mensagemRetorno;
    }
}
