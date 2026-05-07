package com.thiago.desafio_estagio.dto;

import com.thiago.desafio_estagio.models.ClientePf;
import com.thiago.desafio_estagio.models.TipoPessoa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// Resposta para clientes do tipo Pessoa Fisica.
public record ClientePfDto(
        UUID id,
        TipoPessoa tipoPessoa,
        String email,
        boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        String nome,
        String cpf,
        String rg,
        LocalDate dataNascimento
) implements ClienteDto {

    // Fabrica para converter a entidade ClientePf no DTO de resposta.
    public static ClientePfDto from(ClientePf pf) {
        return new ClientePfDto(
                pf.getId(), pf.getTipoPessoa(), pf.getEmail(), pf.isAtivo(),
                pf.getCriadoEm(), pf.getAtualizadoEm(),
                pf.getNome(), pf.getCpf(), pf.getRg(), pf.getDataNascimento()
        );
    }
}
