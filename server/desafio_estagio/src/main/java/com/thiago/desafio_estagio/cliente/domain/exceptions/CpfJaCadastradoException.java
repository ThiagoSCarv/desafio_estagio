package com.thiago.desafio_estagio.cliente.domain.exceptions;

public class CpfJaCadastradoException extends RuntimeException {

    public CpfJaCadastradoException() {
        super("CPF já cadastrado");
    }
}
