package com.thiago.desafio_estagio.cliente.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClientePjRepository extends JpaRepository<ClientePj, UUID> {

    boolean existsByCnpj(String cnpj);

    boolean existsByRazaoSocial(String razaoSocial);
}
