package com.thiago.desafio_estagio.dto;

import com.thiago.desafio_estagio.models.TipoPessoa;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ClienteListDto {

    private UUID id;
    private TipoPessoa tipoPessoa;
    private String email;
    private boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    // Campos exclusivos de ClientePf
    private String nome;
    private String cpf;
    private String rg;
    private LocalDate dataNascimento;

    // Campos exclusivos de ClientePj
    private String cnpj;
    private String razaoSocial;
    private String inscricaoEstatual;
    private LocalDate dataCriacao;
}
