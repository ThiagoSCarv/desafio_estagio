package com.thiago.desafio_estagio.cliente.application;

import jakarta.validation.constraints.Email;

// Todos os campos são opcionais (PATCH parcial). Somente os não-nulos são aplicados no service.
public record ClientePjUpdateDto(
        @Email String email,
        String razaoSocial,
        String inscricaoEstadual,
        Boolean ativo
) {
}
