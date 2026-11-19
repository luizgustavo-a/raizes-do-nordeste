package br.com.uninter.raizesnordeste.application.service;

import br.com.uninter.raizesnordeste.domain.entity.AuditoriaLog;
import br.com.uninter.raizesnordeste.domain.repository.AuditoriaLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaLogRepository auditoriaLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String acao, String entidade, Long entidadeId, String usuarioEmail, String detalhes) {
        AuditoriaLog log = new AuditoriaLog(acao, entidade, entidadeId, usuarioEmail, detalhes);
        auditoriaLogRepository.save(log);
    }
}
