package com.thiago.desafio_estagio.cliente.application;

import com.thiago.desafio_estagio.cliente.domain.ClientePj;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.endereco.application.EnderecoDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

//Response para clientes do tipo Pessoa Juridica.
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
        LocalDate dataCriacao,
        List<EnderecoDto> enderecos
) implements ClienteDto {

    //Converte a entidade ClientePj no DTO de resposta.
    public static ClientePjDto from(ClientePj pj, List<EnderecoDto> enderecos) {
        return new ClientePjDto(
                pj.getId(), pj.getTipoPessoa(), pj.getEmail(), pj.isAtivo(),
                pj.getCriadoEm(), pj.getAtualizadoEm(),
                pj.getCnpj(), pj.getRazaoSocial(), pj.getInscricaoEstadual(), pj.getDataCriacao(),
                enderecos
        );
    }
}
