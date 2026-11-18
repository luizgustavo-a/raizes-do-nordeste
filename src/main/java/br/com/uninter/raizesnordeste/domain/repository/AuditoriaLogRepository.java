package br.com.uninter.raizesnordeste.domain.repository;

import br.com.uninter.raizesnordeste.domain.entity.AuditoriaLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Long> {
    Page<AuditoriaLog> findAllByOrderByDataHoraDesc(Pageable pageable);
    List<AuditoriaLog> findByEntidadeAndEntidadeId(String entidade, Long entidadeId);
}
