package com.thiago.desafio_estagio.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

// Endereço pertence a um Cliente via FK cliente_id (relação unidirecional N:1).
// O cascade ON DELETE no banco garante que ao remover um cliente seus endereços sejam apagados.
@Entity
@Table(name = "enderecos")
@Getter
@Setter
@NoArgsConstructor
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String logradouro;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false, length = 9)
    private String cep;

    @Column(nullable = false)
    private String bairro;

    private String telefone;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(name = "endereco_principal", nullable = false)
    private boolean enderecoPrincipal = false;

    private String complemento;

    // Lado dono da relação. Ignorado na serialização para evitar exposição do cliente em respostas de Endereco.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cliente cliente;
}
