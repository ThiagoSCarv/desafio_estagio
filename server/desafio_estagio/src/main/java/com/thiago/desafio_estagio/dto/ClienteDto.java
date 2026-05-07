package com.thiago.desafio_estagio.dto;

import com.thiago.desafio_estagio.models.TipoPessoa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public sealed interface ClienteDto permits ClientePfDto, ClientePjDto {
    UUID id();
    TipoPessoa tipoPessoa();
    String email();
    boolean ativo();
    LocalDateTime criadoEm();
    LocalDateTime atualizadoEm();
    List<EnderecoDto> enderecos();
}
