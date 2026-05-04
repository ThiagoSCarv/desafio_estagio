package com.thiago.desafio_estagio.repository;

import com.thiago.desafio_estagio.models.ClientePf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClientePfRepository extends JpaRepository<ClientePf, UUID> {

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByRg(String rg);
}
