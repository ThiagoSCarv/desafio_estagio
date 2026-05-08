package com.thiago.desafio_estagio.relatorio.application.exceptions;

public class FormatoRelatorioInvalidoException extends RuntimeException {

    public FormatoRelatorioInvalidoException(String formato) {
        super("Formato de relatório inválido: '" + formato + "'. Valores aceitos: pdf, xlsx.");
    }
}
