package br.com.uninter.raizesnordeste.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Resumo dos dados do usuário autenticado retornados no login e cadastro.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO implements Serializable {

    private Long id;
    private String nome;
    private String perfil;
}
