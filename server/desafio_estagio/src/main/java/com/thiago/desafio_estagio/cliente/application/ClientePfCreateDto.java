package com.thiago.desafio_estagio.cliente.application;

import com.thiago.desafio_estagio.endereco.application.EnderecoCreateDto;
import com.thiago.desafio_estagio.shared.validation.annotation.ValidCpf;
import com.thiago.desafio_estagio.shared.validation.annotation.ValidRg;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ClientePfCreateDto(
        @NotBlank @Email String email,
        @NotBlank String nome,
        @NotBlank @ValidCpf String cpf,
        @NotBlank @ValidRg String rg,
        @NotNull LocalDate dataNascimento,
        List<@Valid EnderecoCreateDto> enderecos
) {
}
