package com.thiago.desafio_estagio.dto;

import com.thiago.desafio_estagio.validation.annotation.ValidCnpj;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ClientePjCreateDto(
        @NotBlank @Email String email,
        @NotBlank @ValidCnpj String cnpj,
        @NotBlank String razaoSocial,
        String inscricaoEstadual,
        @NotNull LocalDate dataCriacao
) {
}
