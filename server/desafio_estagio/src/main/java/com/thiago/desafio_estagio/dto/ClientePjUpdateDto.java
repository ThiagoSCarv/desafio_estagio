package com.thiago.desafio_estagio.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ClientePjUpdateDto {

    @Email
    private String email;

    private Boolean ativo;
}
