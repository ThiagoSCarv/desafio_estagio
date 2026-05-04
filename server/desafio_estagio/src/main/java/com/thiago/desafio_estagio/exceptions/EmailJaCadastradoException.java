package com.thiago.desafio_estagio.exceptions;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException() {
        super("Email já cadastrado");
    }
}
