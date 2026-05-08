package com.thiago.desafio_estagio.shared.exceptions;

import java.util.List;

// Envelope unico para todos os erros retornados pela API. Mantem o contrato uniforme
// (sempre { errors: [...] }) para que clientes (REST/Wicket/Angular) nao precisem
// distinguir resposta unica de lista.
public record ErrorResponse(List<ErrorMessageDTO> errors) {

    public static ErrorResponse of(String field, String message) {
        return new ErrorResponse(List.of(new ErrorMessageDTO(field, message)));
    }

    public static ErrorResponse of(List<ErrorMessageDTO> errors) {
        return new ErrorResponse(errors);
    }
}
