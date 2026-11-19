package br.com.uninter.raizesnordeste.application.dto;

import br.com.uninter.raizesnordeste.domain.enums.PerfilEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class CadastroUsuarioDTO implements Serializable {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 150, message = "O nome deve conter entre 3 e 150 caracteres.")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail informado possui formato inválido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres.")
    private String senha;

    private PerfilEnum perfil = PerfilEnum.CLIENTE;

    private String telefone;

    /**
     * Requisito LGPD: Consentimento explícito do titular
     */
    @NotNull(message = "O consentimento dos termos de privacidade (LGPD) é obrigatório.")
    private Boolean consentimentoLgpd;
}
