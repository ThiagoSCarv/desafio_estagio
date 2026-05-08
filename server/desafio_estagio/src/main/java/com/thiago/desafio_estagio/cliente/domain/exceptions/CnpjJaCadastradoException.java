package com.thiago.desafio_estagio.cliente.domain.exceptions;

public class CnpjJaCadastradoException extends RuntimeException {

    public CnpjJaCadastradoException() {
        super("CNPJ já cadastrado");
    }
}
