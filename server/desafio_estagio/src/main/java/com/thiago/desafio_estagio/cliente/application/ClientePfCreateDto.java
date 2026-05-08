package com.thiago.desafio_estagio.dto;

import com.thiago.desafio_estagio.validation.annotation.ValidCpf;
import com.thiago.desafio_estagio.validation.annotation.ValidRg;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ClientePfCreateDto(
        @NotBlank @Email String email,
        @NotBlank String nome,
        @NotBlank @ValidCpf String cpf,
        @NotBlank @ValidRg String rg,
        @NotNull LocalDate dataNascimento
) {
}
