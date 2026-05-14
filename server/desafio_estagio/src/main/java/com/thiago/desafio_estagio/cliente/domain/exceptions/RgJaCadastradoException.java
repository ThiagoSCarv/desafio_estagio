package com.thiago.desafio_estagio.cliente.domain.exceptions;

import com.thiago.desafio_estagio.shared.exceptions.DuplicidadeException;

public class RgJaCadastradoException extends DuplicidadeException {

    public RgJaCadastradoException() {
        super("RG já cadastrado");
    }

    @Override
    public String field() {
        return "rg";
    }
}
