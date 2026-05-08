package com.thiago.desafio_estagio.cliente.application;

import jakarta.validation.constraints.Email;

public record ClientePfUpdateDto(
        @Email String email,
        String nome,
        Boolean ativo
) {
}
