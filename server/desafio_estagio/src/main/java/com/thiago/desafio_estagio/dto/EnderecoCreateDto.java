package com.thiago.desafio_estagio.dto;

import com.thiago.desafio_estagio.validation.annotation.ValidCep;
import com.thiago.desafio_estagio.validation.annotation.ValidTelefone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EnderecoCreateDto {

    @NotBlank
    private String logradouro;

    @NotBlank
    private String numero;

    @NotBlank
    @ValidCep
    private String cep;

    @NotBlank
    private String bairro;

    @ValidTelefone
    private String telefone;

    @NotBlank
    private String cidade;

    @NotBlank
    @Size(min = 2, max = 2)
    private String estado;

    @NotNull
    private Boolean enderecoPrincipal;

    private String complemento;
}
