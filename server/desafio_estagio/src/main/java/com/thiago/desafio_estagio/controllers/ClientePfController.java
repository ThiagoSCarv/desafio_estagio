package com.thiago.desafio_estagio.controllers;

import com.thiago.desafio_estagio.dto.ClientePfCreateDto;
import com.thiago.desafio_estagio.dto.ClientePfUpdateDto;
import com.thiago.desafio_estagio.models.ClientePf;
import com.thiago.desafio_estagio.service.ClientePfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/clientes")
public class ClientePfController {

    @Autowired
    private ClientePfService clientePfService;

    @PostMapping("/pf")
    public ResponseEntity<?> criar(@RequestBody @Valid ClientePfCreateDto dto) {
        try {
            ClientePf clientePf = clientePfService.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(clientePf);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/pf/{id}")
    public ResponseEntity<?> atualizar(@PathVariable UUID id, @RequestBody @Valid ClientePfUpdateDto dto) {
        try {
            ClientePf clientePf = clientePfService.atualizar(id, dto);
            return ResponseEntity.ok(clientePf);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
