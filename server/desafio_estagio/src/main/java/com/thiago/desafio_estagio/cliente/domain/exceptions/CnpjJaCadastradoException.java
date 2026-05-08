package com.thiago.desafio_estagio.exceptions;

public class CnpjJaCadastradoException extends RuntimeException {

    public CnpjJaCadastradoException() {
        super("CNPJ já cadastrado");
    }
}
