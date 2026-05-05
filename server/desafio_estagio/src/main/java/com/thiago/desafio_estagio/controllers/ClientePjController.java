package com.thiago.desafio_estagio.controllers;

import com.thiago.desafio_estagio.dto.ClientePjCreateDto;
import com.thiago.desafio_estagio.dto.ClientePjUpdateDto;
import com.thiago.desafio_estagio.models.ClientePj;
import com.thiago.desafio_estagio.service.ClientePjService;
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
public class ClientePjController {

    @Autowired
    private ClientePjService clientePjService;

    @PostMapping("/pj")
    public ResponseEntity<?> criar(@RequestBody @Valid ClientePjCreateDto dto) {
        try {
            ClientePj clientePj = clientePjService.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(clientePj);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/pj/{id}")
    public ResponseEntity<?> atualizar(@PathVariable UUID id, @RequestBody @Valid ClientePjUpdateDto dto) {
        try {
            ClientePj clientePj = clientePjService.atualizar(id, dto);
            return ResponseEntity.ok(clientePj);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
