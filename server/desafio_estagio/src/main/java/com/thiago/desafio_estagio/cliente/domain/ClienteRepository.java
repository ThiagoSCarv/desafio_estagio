package com.thiago.desafio_estagio.cliente.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID>, ClienteRepositoryCustom {

    // Cobre PF e PJ na mesma query pois email fica na tabela pai clientes.
    boolean existsByEmail(String email);

    // Usado no update para rejeitar duplicata excluindo o próprio registro.
    boolean existsByEmailAndIdNot(String email, UUID id);

    long countByAtivoTrue();
}
