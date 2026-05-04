package com.thiago.desafio_estagio.dto;

import com.thiago.desafio_estagio.validation.annotation.ValidCpf;
import com.thiago.desafio_estagio.validation.annotation.ValidRg;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ClientePfCreateDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String nome;

    @NotBlank
    @ValidCpf
    private String cpf;

    @NotBlank
    @ValidRg
    private String rg;

    @NotNull
    private LocalDate dataNascimento;

    @Valid
    private List<EnderecoCreateDto> enderecos;
}
