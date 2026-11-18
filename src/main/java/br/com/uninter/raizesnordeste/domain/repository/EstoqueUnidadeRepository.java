package br.com.uninter.raizesnordeste.domain.repository;

import br.com.uninter.raizesnordeste.domain.entity.EstoqueUnidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueUnidadeRepository extends JpaRepository<EstoqueUnidade, Long> {
    
    Optional<EstoqueUnidade> findByUnidadeIdAndProdutoId(Long unidadeId, Long produtoId);

    @Query("SELECT e FROM EstoqueUnidade e WHERE e.unidade.id = :unidadeId AND e.produto.ativo = true")
    List<EstoqueUnidade> findByUnidadeId(@Param("unidadeId") Long unidadeId);
}
