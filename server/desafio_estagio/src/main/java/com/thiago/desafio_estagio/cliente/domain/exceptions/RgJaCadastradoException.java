package com.thiago.desafio_estagio.cliente.domain.exceptions;

public class RgJaCadastradoException extends RuntimeException {

    public RgJaCadastradoException() {
        super("RG já cadastrado");
    }
}
