package br.com.uninter.raizesnordeste.domain.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Atendimento explícito aos critérios de LGPD (Art. 7º, I da Lei 13.709/2018).
 * Registra o consentimento livre, informado e inequívoco para tratamento de dados.
 */
@Entity
@Table(name = "tb_consentimento_lgpd")
public class ConsentimentoLgpd implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 255)
    private String finalidade;

    @Column(nullable = false)
    private Boolean consentido = true;

    @Column(name = "data_consentimento", nullable = false)
    private LocalDateTime dataConsentimento = LocalDateTime.now();

    @Column(name = "ip_origem", length = 45)
    private String ipOrigem;

    public ConsentimentoLgpd() {
    }

    public ConsentimentoLgpd(Usuario usuario, String finalidade, Boolean consentido, String ipOrigem) {
        this.usuario = usuario;
        this.finalidade = finalidade;
        this.consentido = consentido;
        this.ipOrigem = ipOrigem;
        this.dataConsentimento = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(String finalidade) {
        this.finalidade = finalidade;
    }

    public Boolean getConsentido() {
        return consentido;
    }

    public void setConsentido(Boolean consentido) {
        this.consentido = consentido;
    }

    public LocalDateTime getDataConsentimento() {
        return dataConsentimento;
    }

    public void setDataConsentimento(LocalDateTime dataConsentimento) {
        this.dataConsentimento = dataConsentimento;
    }

    public String getIpOrigem() {
        return ipOrigem;
    }

    public void setIpOrigem(String ipOrigem) {
        this.ipOrigem = ipOrigem;
    }
}
