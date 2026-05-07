package com.thiago.desafio_estagio.dto;

import com.thiago.desafio_estagio.models.ClientePj;
import com.thiago.desafio_estagio.models.TipoPessoa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// Resposta para clientes do tipo Pessoa Juridica.
public record ClientePjDto(
        UUID id,
        TipoPessoa tipoPessoa,
        String email,
        boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        String cnpj,
        String razaoSocial,
        String inscricaoEstadual,
        LocalDate dataCriacao
) implements ClienteDto {

    // Fabrica para converter a entidade ClientePj no DTO de resposta.
    public static ClientePjDto from(ClientePj pj) {
        return new ClientePjDto(
                pj.getId(), pj.getTipoPessoa(), pj.getEmail(), pj.isAtivo(),
                pj.getCriadoEm(), pj.getAtualizadoEm(),
                pj.getCnpj(), pj.getRazaoSocial(), pj.getInscricaoEstadual(), pj.getDataCriacao()
        );
    }
}
