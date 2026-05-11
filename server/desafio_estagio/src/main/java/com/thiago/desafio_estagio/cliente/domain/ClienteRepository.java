package com.thiago.desafio_estagio.cliente.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID>, ClienteRepositoryCustom {

    //Verifica em toda a tabela Cliente se existe somente um email
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
