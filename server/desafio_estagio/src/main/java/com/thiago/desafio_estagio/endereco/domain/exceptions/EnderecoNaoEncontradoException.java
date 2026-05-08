package com.thiago.desafio_estagio.endereco.domain.exceptions;

public class EnderecoNaoEncontradoException extends RuntimeException {

    public EnderecoNaoEncontradoException() {
        super("Endereço não encontrado");
    }
}
