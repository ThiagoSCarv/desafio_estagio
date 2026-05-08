package com.thiago.desafio_estagio.exceptions;

public class CpfJaCadastradoException extends RuntimeException {

    public CpfJaCadastradoException() {
        super("CPF já cadastrado");
    }
}
