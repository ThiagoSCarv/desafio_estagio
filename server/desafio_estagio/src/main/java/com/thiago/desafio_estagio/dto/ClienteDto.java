package com.thiago.desafio_estagio.dto;

import com.thiago.desafio_estagio.models.TipoPessoa;

import java.time.LocalDateTime;
import java.util.UUID;

// Tipo polimorfico de resposta para Cliente. As implementacoes (ClientePfDto e ClientePjDto)
// carregam apenas os campos do tipo correspondente, eliminando os campos sempre nulos da abordagem
// "frankenstein" anterior.
public sealed interface ClienteDto permits ClientePfDto, ClientePjDto {
    UUID id();
    TipoPessoa tipoPessoa();
    String email();
    boolean ativo();
    LocalDateTime criadoEm();
    LocalDateTime atualizadoEm();
}
