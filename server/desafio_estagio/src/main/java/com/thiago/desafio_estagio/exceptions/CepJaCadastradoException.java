package com.thiago.desafio_estagio.exceptions;

public class CepJaCadastradoException extends RuntimeException {

    public CepJaCadastradoException() {
        super("CEP já cadastrado para este cliente");
    }
}
