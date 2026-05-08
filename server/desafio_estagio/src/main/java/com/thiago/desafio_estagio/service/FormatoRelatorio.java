package com.thiago.desafio_estagio.service;

import com.thiago.desafio_estagio.exceptions.FormatoRelatorioInvalidoException;
import org.springframework.http.MediaType;

// Formatos suportados para a geração de relatórios.
// Cada valor carrega a extensão de arquivo e o Content-Type usados nas respostas HTTP,
// centralizando esses metadados longe do controller.
public enum FormatoRelatorio {

    PDF("pdf", MediaType.APPLICATION_PDF),
    XLSX("xlsx", MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

    private final String extensao;
    private final MediaType mediaType;

    FormatoRelatorio(String extensao, MediaType mediaType) {
        this.extensao = extensao;
        this.mediaType = mediaType;
    }

    public String extensao() {
        return extensao;
    }

    public MediaType mediaType() {
        return mediaType;
    }

    // Conversão case-insensitive a partir do query param. Lança exceção dedicada
    // para que o handler global devolva 400 com mensagem amigável em vez do erro
    // genérico do Spring para enums inválidos.
    public static FormatoRelatorio from(String valor) {
        if (valor == null || valor.isBlank()) {
            return PDF;
        }
        try {
            return FormatoRelatorio.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new FormatoRelatorioInvalidoException(valor);
        }
    }
}
