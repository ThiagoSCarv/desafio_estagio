package com.thiago.desafio_estagio.dto;

import jakarta.validation.constraints.Email;

public record ClientePfUpdateDto(
        @Email String email,
        String nome,
        Boolean ativo
) {
}
