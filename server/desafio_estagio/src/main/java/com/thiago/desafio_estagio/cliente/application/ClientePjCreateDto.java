package com.thiago.desafio_estagio.cliente.application;

import com.thiago.desafio_estagio.endereco.application.EnderecoCreateDto;
import com.thiago.desafio_estagio.shared.validation.annotation.ValidCnpj;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

// Payload de criação de PJ. CNPJ é normalizado no service antes de persistir.
public record ClientePjCreateDto(
        @NotBlank @Email String email,
        @NotBlank @ValidCnpj String cnpj,
        @NotBlank String razaoSocial,
        String inscricaoEstadual,
        @NotNull LocalDate dataCriacao,
        List<@Valid EnderecoCreateDto> enderecos
) {
}
