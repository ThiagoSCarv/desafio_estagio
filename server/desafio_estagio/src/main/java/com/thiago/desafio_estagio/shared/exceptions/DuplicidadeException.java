package com.thiago.desafio_estagio.shared.exceptions;

// Classe base para excecoes de duplicidade de campo unico.
// Permite um handler generico no ExceptionHandlerController em vez de um por campo.
public abstract class DuplicidadeException extends RuntimeException {

    protected DuplicidadeException(String message) {
        super(message);
    }

    public abstract String field();
}
