package com.thiago.desafio_estagio.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ClientePfUpdateDto {

    @Email
    private String email;

    private String nome;

    private Boolean ativo;
}
