package com.thiago.desafio_estagio.exceptions;

public class RazaoSocialJaCadastradaException extends RuntimeException {

    public RazaoSocialJaCadastradaException() {
        super("Razão social já cadastrada");
    }
}
