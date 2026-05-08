package com.thiago.desafio_estagio.cliente.domain.exceptions;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException() {
        super("Email já cadastrado");
    }
}
