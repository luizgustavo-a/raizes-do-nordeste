package br.com.uninter.raizesnordeste.application.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AuditoriaResponseDTO implements Serializable {

    private Long id;
    private String acao;
    private String entidade;
    private Long entidadeId;
    private String usuarioResponsavel;
    private String detalhes;
    private LocalDateTime dataHora;

    public AuditoriaResponseDTO() {
    }

    public AuditoriaResponseDTO(Long id, String acao, String entidade, Long entidadeId,
                                String usuarioResponsavel, String detalhes, LocalDateTime dataHora) {
        this.id = id;
        this.acao = acao;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.usuarioResponsavel = usuarioResponsavel;
        this.detalhes = detalhes;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getEntidade() {
        return entidade;
    }

    public void setEntidade(String entidade) {
        this.entidade = entidade;
    }

    public Long getEntidadeId() {
        return entidadeId;
    }

    public void setEntidadeId(Long entidadeId) {
        this.entidadeId = entidadeId;
    }

    public String getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(String usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
