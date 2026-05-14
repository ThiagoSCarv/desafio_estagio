package com.thiago.desafio_estagio.cliente.domain.exceptions;

import com.thiago.desafio_estagio.shared.exceptions.DuplicidadeException;

public class RazaoSocialJaCadastradaException extends DuplicidadeException {

    public RazaoSocialJaCadastradaException() {
        super("Razão social já cadastrada");
    }

    @Override
    public String field() {
        return "razaoSocial";
    }
}
