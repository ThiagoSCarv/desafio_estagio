package com.thiago.desafio_estagio.cliente.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// Subtipo de Cliente para Pessoa Física. O id é FK para a tabela clientes (herança JOINED).
@Entity
@Table(name = "clientes_pf")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
public class ClientePf extends Cliente {

    @Column(nullable = false)
    private String nome;

    // Armazenado somente com dígitos (length = 11). O service normaliza antes de salvar.
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    // Armazenado alfanumérico maiúsculo, sem pontos/traços.
    @Column(nullable = false)
    private String rg;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;
}
