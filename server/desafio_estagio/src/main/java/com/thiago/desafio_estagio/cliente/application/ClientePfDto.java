package com.thiago.desafio_estagio.cliente.application;

import com.thiago.desafio_estagio.cliente.domain.ClientePf;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.endereco.application.EnderecoDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

//Response para clientes do tipo Pessoa Fisica.
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
        LocalDate dataNascimento,
        List<EnderecoDto> enderecos
) implements ClienteDto {

    //Converte a entidade ClientePf no DTO de resposta.
    public static ClientePfDto from(ClientePf pf, List<EnderecoDto> enderecos) {
        return new ClientePfDto(
                pf.getId(), pf.getTipoPessoa(), pf.getEmail(), pf.isAtivo(),
                pf.getCriadoEm(), pf.getAtualizadoEm(),
                pf.getNome(), pf.getCpf(), pf.getRg(), pf.getDataNascimento(),
                enderecos
        );
    }
}
