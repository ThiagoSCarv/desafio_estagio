package com.thiago.desafio_estagio.cliente.web.rest;

import com.thiago.desafio_estagio.cliente.application.ClientePjCreateDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjService;
import com.thiago.desafio_estagio.cliente.application.ClientePjUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

//Rota especifica para clientes do tipo pessoa juridica
@RestController
@RequestMapping("/clientes/pj")
@RequiredArgsConstructor
public class ClientePjController {

    private final ClientePjService clientePjService;

    @PostMapping
    public ResponseEntity<ClientePjDto> criar(@RequestBody @Valid ClientePjCreateDto dto) {
        ClientePjDto clientePj = clientePjService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientePj);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClientePjDto> atualizar(@PathVariable UUID id, @RequestBody @Valid ClientePjUpdateDto dto) {
        ClientePjDto clientePj = clientePjService.atualizar(id, dto);
        return ResponseEntity.ok(clientePj);
    }
}
