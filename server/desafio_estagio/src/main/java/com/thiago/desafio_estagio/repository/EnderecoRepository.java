package com.thiago.desafio_estagio.repository;

import com.thiago.desafio_estagio.models.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {

    boolean existsByClienteIdAndCep(UUID clienteId, String cep);

    // Busca todos os endereços vinculados a um cliente (relação unidirecional N:1).
    List<Endereco> findByClienteId(UUID clienteId);

    // Desmarca todos os endereços principais de um cliente em uma única operação.
    // Usado antes de gravar um novo endereço principal, garantindo a invariante de no maximo um principal por cliente.
    @Modifying
    @Query("UPDATE Endereco e SET e.enderecoPrincipal = false WHERE e.cliente.id = :clienteId")
    void desmarcarTodosPrincipaisDoCliente(UUID clienteId);
}
