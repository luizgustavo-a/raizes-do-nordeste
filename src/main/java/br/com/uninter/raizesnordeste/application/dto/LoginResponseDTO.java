package br.com.uninter.raizesnordeste.application.dto;

import java.io.Serializable;

/**
 * DTO de resposta de autenticação com token JWT e resumo do usuário.
 */
public class LoginResponseDTO implements Serializable {

    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserDetailsDTO user;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String accessToken, Long expiresIn, UserDetailsDTO user) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserDetailsDTO getUser() {
        return user;
    }

    public void setUser(UserDetailsDTO user) {
        this.user = user;
    }

    public static class UserDetailsDTO implements Serializable {
        private Long id;
        private String nome;
        private String perfil;

        public UserDetailsDTO() {
        }

        public UserDetailsDTO(Long id, String nome, String perfil) {
            this.id = id;
            this.nome = nome;
            this.perfil = perfil;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getPerfil() {
            return perfil;
        }

        public void setPerfil(String perfil) {
            this.perfil = perfil;
        }
    }
}
