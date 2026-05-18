package com.thiago.desafio_estagio.endereco.domain.exceptions;

public class CepNaoEncontradoException extends RuntimeException {

    public CepNaoEncontradoException() {
        super("CEP não encontrado");
    }
}
