package com.thiago.desafio_estagio.relatorio.application.exceptions;

public class RelatorioException extends RuntimeException {

    public RelatorioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public RelatorioException(String mensagem) {
        super(mensagem);
    }
}
