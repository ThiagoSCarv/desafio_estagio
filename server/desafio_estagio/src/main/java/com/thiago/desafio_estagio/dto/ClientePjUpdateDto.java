package com.thiago.desafio_estagio.dto;

import jakarta.validation.constraints.Email;

public record ClientePjUpdateDto(
        @Email String email,
        String razaoSocial,
        String inscricaoEstadual,
        Boolean ativo
) {
}
