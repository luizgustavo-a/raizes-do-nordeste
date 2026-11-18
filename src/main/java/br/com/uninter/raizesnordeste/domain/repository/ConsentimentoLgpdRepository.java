package br.com.uninter.raizesnordeste.domain.repository;

import br.com.uninter.raizesnordeste.domain.entity.ConsentimentoLgpd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsentimentoLgpdRepository extends JpaRepository<ConsentimentoLgpd, Long> {
    List<ConsentimentoLgpd> findByUsuarioId(Long usuarioId);
}
