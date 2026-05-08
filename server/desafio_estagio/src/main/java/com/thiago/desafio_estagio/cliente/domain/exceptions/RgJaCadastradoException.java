package com.thiago.desafio_estagio.exceptions;

public class RgJaCadastradoException extends RuntimeException {

    public RgJaCadastradoException() {
        super("RG já cadastrado");
    }
}
