package br.com.uninter.raizesnordeste.infrastructure.exception;

import java.io.Serializable;

public class FieldErrorDTO implements Serializable {

    private String field;
    private String issue;

    public FieldErrorDTO() {
    }

    public FieldErrorDTO(String field, String issue) {
        this.field = field;
        this.issue = issue;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
