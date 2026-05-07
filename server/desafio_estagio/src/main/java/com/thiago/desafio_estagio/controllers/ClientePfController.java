package com.thiago.desafio_estagio.controllers;

import com.thiago.desafio_estagio.dto.ClientePfCreateDto;
import com.thiago.desafio_estagio.dto.ClientePfDto;
import com.thiago.desafio_estagio.dto.ClientePfUpdateDto;
import com.thiago.desafio_estagio.service.ClientePfService;
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

@RestController
@RequestMapping("/clientes/pf")
@RequiredArgsConstructor
public class ClientePfController {

    private final ClientePfService clientePfService;

    @PostMapping
    public ResponseEntity<ClientePfDto> criar(@RequestBody @Valid ClientePfCreateDto dto) {
        ClientePfDto clientePf = clientePfService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientePf);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClientePfDto> atualizar(@PathVariable UUID id, @RequestBody @Valid ClientePfUpdateDto dto) {
        ClientePfDto clientePf = clientePfService.atualizar(id, dto);
        return ResponseEntity.ok(clientePf);
    }
}
