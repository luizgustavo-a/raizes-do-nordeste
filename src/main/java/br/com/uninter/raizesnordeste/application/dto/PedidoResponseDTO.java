package br.com.uninter.raizesnordeste.application.dto;

import br.com.uninter.raizesnordeste.domain.enums.CanalPedidoEnum;
import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    public PedidoResponseDTO() {
    }

    public PedidoResponseDTO(Long pedidoId, Long unidadeId, String unidadeNome, Long clienteId, String clienteNome,
                             CanalPedidoEnum canalPedido, StatusPedidoEnum status, BigDecimal total,
                             List<ItemPedidoResponseDTO> itens, LocalDateTime createdAt) {
        this.pedidoId = pedidoId;
        this.unidadeId = unidadeId;
        this.unidadeNome = unidadeNome;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.canalPedido = canalPedido;
        this.status = status;
        this.total = total;
        this.itens = itens;
        this.createdAt = createdAt;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public String getUnidadeNome() {
        return unidadeNome;
    }

    public void setUnidadeNome(String unidadeNome) {
        this.unidadeNome = unidadeNome;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public CanalPedidoEnum getCanalPedido() {
        return canalPedido;
    }

    public void setCanalPedido(CanalPedidoEnum canalPedido) {
        this.canalPedido = canalPedido;
    }

    public StatusPedidoEnum getStatus() {
        return status;
    }

    public void setStatus(StatusPedidoEnum status) {
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<ItemPedidoResponseDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoResponseDTO> itens) {
        this.itens = itens;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
