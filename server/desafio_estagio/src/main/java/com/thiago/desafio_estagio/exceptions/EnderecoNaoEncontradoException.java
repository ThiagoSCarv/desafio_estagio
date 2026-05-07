package com.thiago.desafio_estagio.exceptions;

public class EnderecoNaoEncontradoException extends RuntimeException {

    public EnderecoNaoEncontradoException() {
        super("Endereço não encontrado");
    }
}
