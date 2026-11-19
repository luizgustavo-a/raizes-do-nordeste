package br.com.uninter.raizesnordeste.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO de resposta de autenticação com token JWT e resumo do usuário.
 */
@Data
@NoArgsConstructor
public class LoginResponseDTO implements Serializable {

    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserDetailsDTO user;

    public LoginResponseDTO(String accessToken, Long expiresIn, UserDetailsDTO user) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
    }
}
