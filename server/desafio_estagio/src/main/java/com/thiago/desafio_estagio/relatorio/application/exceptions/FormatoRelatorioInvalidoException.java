package com.thiago.desafio_estagio.exceptions;

public class FormatoRelatorioInvalidoException extends RuntimeException {

    public FormatoRelatorioInvalidoException(String formato) {
        super("Formato de relatório inválido: '" + formato + "'. Valores aceitos: pdf, xlsx.");
    }
}
