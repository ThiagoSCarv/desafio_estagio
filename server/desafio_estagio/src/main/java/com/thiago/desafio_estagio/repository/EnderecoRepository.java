package com.thiago.desafio_estagio.repository;

import com.thiago.desafio_estagio.models.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {

    boolean existsByClienteIdAndCep(UUID clienteId, String cep);
}
