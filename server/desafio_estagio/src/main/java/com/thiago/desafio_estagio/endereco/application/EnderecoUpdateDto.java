package com.thiago.desafio_estagio.dto;

import com.thiago.desafio_estagio.validation.annotation.ValidTelefone;

public record EnderecoUpdateDto(
        String numero,
        @ValidTelefone String telefone,
        Boolean enderecoPrincipal,
        String complemento
) {
}
