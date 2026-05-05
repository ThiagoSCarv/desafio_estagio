package com.thiago.desafio_estagio.repository;

import com.thiago.desafio_estagio.models.ClientePj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClientePjRepository extends JpaRepository<ClientePj, UUID> {

    boolean existsByEmail(String email);

    boolean existsByCnpj(String cnpj);

    boolean existsByRazaoSocial(String razaoSocial);
}
