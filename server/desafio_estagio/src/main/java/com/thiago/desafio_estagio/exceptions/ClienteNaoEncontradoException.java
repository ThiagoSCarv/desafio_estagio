package com.thiago.desafio_estagio.exceptions;

public class ClienteNaoEncontradoException extends RuntimeException {

    public ClienteNaoEncontradoException() {
        super("Cliente não encontrado");
    }
}
