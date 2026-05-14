package com.thiago.desafio_estagio.cliente.domain.exceptions;

import com.thiago.desafio_estagio.shared.exceptions.DuplicidadeException;

public class CnpjJaCadastradoException extends DuplicidadeException {

    public CnpjJaCadastradoException() {
        super("CNPJ já cadastrado");
    }

    @Override
    public String field() {
        return "cnpj";
    }
}
