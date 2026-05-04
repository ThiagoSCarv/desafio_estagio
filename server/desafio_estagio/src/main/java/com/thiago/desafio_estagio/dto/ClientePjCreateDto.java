package com.thiago.desafio_estagio.dto;

import com.thiago.desafio_estagio.validation.annotation.ValidCnpj;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ClientePjCreateDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @ValidCnpj
    private String cnpj;

    @NotBlank
    private String razaoSocial;

    private String inscricaoEstatual;

    @NotNull
    private LocalDate dataCriacao;

    @Valid
    private List<EnderecoCreateDto> enderecos;
}
