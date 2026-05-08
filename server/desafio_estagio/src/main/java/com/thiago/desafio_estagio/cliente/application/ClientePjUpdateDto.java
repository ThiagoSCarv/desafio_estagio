package com.thiago.desafio_estagio.cliente.application;

import jakarta.validation.constraints.Email;

public record ClientePjUpdateDto(
        @Email String email,
        String razaoSocial,
        String inscricaoEstadual,
        Boolean ativo
) {
}
