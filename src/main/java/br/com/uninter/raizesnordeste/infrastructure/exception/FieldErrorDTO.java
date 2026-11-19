package br.com.uninter.raizesnordeste.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorDTO implements Serializable {

    private String field;
    private String issue;
}
