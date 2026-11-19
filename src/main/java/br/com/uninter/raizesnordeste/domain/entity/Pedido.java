package br.com.uninter.raizesnordeste.domain.entity;

import br.com.uninter.raizesnordeste.domain.enums.CanalPedidoEnum;
import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_pedido")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    /**
     * Campo obrigatório canalPedido (ENUM: APP, TOTEM, BALCAO, PICKUP, WEB).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "canal_pedido", nullable = false, length = 20)
    private CanalPedidoEnum canalPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusPedidoEnum status = StatusPedidoEnum.AGUARDANDO_PAGAMENTO;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido(Long id, Unidade unidade, Usuario cliente, CanalPedidoEnum canalPedido, StatusPedidoEnum status, BigDecimal valorTotal) {
        this.id = id;
        this.unidade = unidade;
        this.cliente = cliente;
        this.canalPedido = canalPedido;
        this.status = status;
        this.valorTotal = valorTotal;
        this.dataCriacao = LocalDateTime.now();
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
    }
}
