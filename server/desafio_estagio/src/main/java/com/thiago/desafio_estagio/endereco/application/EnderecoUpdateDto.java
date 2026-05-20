package com.thiago.desafio_estagio.endereco.application;

import com.thiago.desafio_estagio.shared.validation.annotation.ValidTelefone;

// PATCH parcial — campos de localização (cep, logradouro, bairro, cidade, estado) são imutáveis após criação.
public record EnderecoUpdateDto(
        String numero,
        @ValidTelefone String telefone,
        Boolean enderecoPrincipal,
        String complemento
) {
}
