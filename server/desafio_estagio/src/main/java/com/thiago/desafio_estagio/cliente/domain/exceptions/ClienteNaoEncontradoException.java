package com.thiago.desafio_estagio.cliente.domain.exceptions;

public class ClienteNaoEncontradoException extends RuntimeException {

    public ClienteNaoEncontradoException() {
        super("Cliente não encontrado");
    }
}
