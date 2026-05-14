package com.thiago.desafio_estagio.cliente.domain.exceptions;

import com.thiago.desafio_estagio.shared.exceptions.DuplicidadeException;

public class CpfJaCadastradoException extends DuplicidadeException {

    public CpfJaCadastradoException() {
        super("CPF já cadastrado");
    }

    @Override
    public String field() {
        return "cpf";
    }
}
