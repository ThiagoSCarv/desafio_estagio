package com.thiago.desafio_estagio.repository;

import com.thiago.desafio_estagio.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID>, JpaSpecificationExecutor<Cliente> {

    //Verifica em toda a tabela Cliente se existe somente um email
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
