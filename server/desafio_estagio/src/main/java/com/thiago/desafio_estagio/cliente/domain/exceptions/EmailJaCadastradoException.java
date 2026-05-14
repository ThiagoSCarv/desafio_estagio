package com.thiago.desafio_estagio.cliente.domain.exceptions;

import com.thiago.desafio_estagio.shared.exceptions.DuplicidadeException;

public class EmailJaCadastradoException extends DuplicidadeException {

    public EmailJaCadastradoException() {
        super("Email já cadastrado");
    }

    @Override
    public String field() {
        return "email";
    }
}
