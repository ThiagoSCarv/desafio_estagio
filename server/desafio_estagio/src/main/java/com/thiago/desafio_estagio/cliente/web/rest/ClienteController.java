package com.thiago.desafio_estagio.controllers;

import com.thiago.desafio_estagio.dto.ClienteDto;
import com.thiago.desafio_estagio.models.TipoPessoa;
import com.thiago.desafio_estagio.service.ClienteService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // Lista clientes com filtros opcionais por tipo, documento e nome/razao social, com paginacao.
    @GetMapping
    public ResponseEntity<Page<ClienteDto>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) TipoPessoa tipoPessoa,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String nome
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClienteDto> resultado = clienteService.listarTodos(tipoPessoa, documento, nome, pageable);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDto> buscar(@PathVariable UUID id) {
        ClienteDto cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
