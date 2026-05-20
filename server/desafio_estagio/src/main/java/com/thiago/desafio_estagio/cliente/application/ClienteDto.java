package com.thiago.desafio_estagio.cliente.application;

import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.endereco.application.EnderecoDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// Sealed interface garante exaustividade em switches: qualquer adição de subtipo
// quebra a compilação em vez de cair num default silencioso.
public sealed interface ClienteDto permits ClientePfDto, ClientePjDto {
    UUID id();
    TipoPessoa tipoPessoa();
    String email();
    boolean ativo();
    LocalDateTime criadoEm();
    LocalDateTime atualizadoEm();
    List<EnderecoDto> enderecos();
}
