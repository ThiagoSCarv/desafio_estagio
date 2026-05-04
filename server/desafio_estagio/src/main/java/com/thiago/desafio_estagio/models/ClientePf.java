package com.thiago.desafio_estagio.models;

import com.thiago.desafio_estagio.validation.annotation.ValidCpf;
import com.thiago.desafio_estagio.validation.annotation.ValidRg;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "clientes_pf")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
public class ClientePf extends Cliente {

    @Column(nullable = false)
    private String nome;

    @ValidCpf
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @ValidRg
    @Column(nullable = false)
    private String rg;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;
}
