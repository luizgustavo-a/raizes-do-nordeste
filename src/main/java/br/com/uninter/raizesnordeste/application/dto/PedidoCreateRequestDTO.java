package br.com.uninter.raizesnordeste.application.dto;

import br.com.uninter.raizesnordeste.domain.enums.CanalPedidoEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * DTO de entrada para criação de pedidos multicanal.
 */
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

    public PedidoCreateRequestDTO() {
    }

    public CanalPedidoEnum getCanalPedido() {
        return canalPedido;
    }

    public void setCanalPedido(CanalPedidoEnum canalPedido) {
        this.canalPedido = canalPedido;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public List<ItemPedidoRequestDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequestDTO> itens) {
        this.itens = itens;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
}
