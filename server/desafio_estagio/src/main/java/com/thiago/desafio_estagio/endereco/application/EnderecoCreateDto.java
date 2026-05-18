package com.thiago.desafio_estagio.endereco.application;

import com.thiago.desafio_estagio.shared.validation.annotation.ValidCep;
import com.thiago.desafio_estagio.shared.validation.annotation.ValidTelefone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EnderecoCreateDto(
        @NotBlank @ValidCep String cep,
        @NotBlank String logradouro,
        @NotBlank String numero,
        @NotBlank String bairro,
        @NotBlank String cidade,
        @NotBlank @Size(min = 2, max = 2) String estado,
        @ValidTelefone String telefone,
        @NotNull Boolean enderecoPrincipal,
        String complemento
) {
}
