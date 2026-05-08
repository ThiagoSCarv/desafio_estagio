package com.thiago.desafio_estagio.endereco.application;

import com.thiago.desafio_estagio.shared.validation.annotation.ValidTelefone;

public record EnderecoUpdateDto(
        String numero,
        @ValidTelefone String telefone,
        Boolean enderecoPrincipal,
        String complemento
) {
}
