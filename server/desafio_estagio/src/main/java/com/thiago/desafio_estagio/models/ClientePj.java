package com.thiago.desafio_estagio.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "clientes_pj")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
public class ClientePj extends Cliente {

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(name = "razao_social", nullable = false, unique = true)
    private String razaoSocial;

    @Column(name = "inscricao_estadual")
    private String inscricaoEstadual;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;
}
