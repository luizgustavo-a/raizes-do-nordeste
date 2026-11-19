package br.com.uninter.raizesnordeste.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Registro da trilha de auditoria para operações sensíveis (criação, cancelamento e alteração de status).
 */
@Entity
@Table(name = "tb_auditoria_log")
@Getter
@Setter
@NoArgsConstructor
public class AuditoriaLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String acao;

    @Column(nullable = false, length = 50)
    private String entidade;

    @Column(name = "entidade_id")
    private Long entidadeId;

    @Column(name = "usuario_responsavel", nullable = false, length = 150)
    private String usuarioResponsavel;

    @Column(columnDefinition = "TEXT")
    private String detalhes;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    public AuditoriaLog(String acao, String entidade, Long entidadeId, String usuarioResponsavel, String detalhes) {
        this.acao = acao;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.usuarioResponsavel = usuarioResponsavel;
        this.detalhes = detalhes;
        this.dataHora = LocalDateTime.now();
    }
}
