package br.com.uninter.raizesnordeste.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaResponseDTO implements Serializable {

    private Long id;
    private String acao;
    private String entidade;
    private Long entidadeId;
    private String usuarioResponsavel;
    private String detalhes;
    private LocalDateTime dataHora;
}
