package com.thiago.desafio_estagio.cliente.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClientePfRepository extends JpaRepository<ClientePf, UUID> {

    boolean existsByCpf(String cpf);

    boolean existsByRg(String rg);
}
