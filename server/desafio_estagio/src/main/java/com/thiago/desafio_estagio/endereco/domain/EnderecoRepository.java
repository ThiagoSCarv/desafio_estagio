package com.thiago.desafio_estagio.endereco.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {

    boolean existsByClienteId(UUID clienteId);

    List<Endereco> findByClienteId(UUID clienteId);

    // Desmarca o endereço principal do cliente
    @Modifying
    @Query("UPDATE Endereco e SET e.enderecoPrincipal = false WHERE e.cliente.id = :clienteId")
    void desmarcarTodosPrincipaisDoCliente(UUID clienteId);
}
