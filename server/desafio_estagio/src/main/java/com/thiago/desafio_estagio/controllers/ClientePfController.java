package com.thiago.desafio_estagio.controllers;

import com.thiago.desafio_estagio.dto.ClientePfCreateDto;
import com.thiago.desafio_estagio.models.ClientePf;
import com.thiago.desafio_estagio.service.ClientePfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
