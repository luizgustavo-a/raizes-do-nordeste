package br.com.uninter.raizesnordeste.domain.repository;

import br.com.uninter.raizesnordeste.domain.entity.Pedido;
import br.com.uninter.raizesnordeste.domain.enums.CanalPedidoEnum;
import br.com.uninter.raizesnordeste.domain.enums.StatusPedidoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT p FROM Pedido p WHERE " +
           "(:canalPedido IS NULL OR p.canalPedido = :canalPedido) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:unidadeId IS NULL OR p.unidade.id = :unidadeId)")
    Page<Pedido> findComFiltros(
            @Param("canalPedido") CanalPedidoEnum canalPedido,
            @Param("status") StatusPedidoEnum status,
            @Param("unidadeId") Long unidadeId,
            Pageable pageable
    );

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens i LEFT JOIN FETCH i.produto WHERE p.id = :id")
    Optional<Pedido> findByIdWithItens(@Param("id") Long id);
}
