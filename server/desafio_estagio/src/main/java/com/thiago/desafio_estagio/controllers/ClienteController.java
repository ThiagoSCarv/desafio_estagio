package com.thiago.desafio_estagio.controllers;

import com.thiago.desafio_estagio.dto.ClienteListDto;
import com.thiago.desafio_estagio.models.TipoPessoa;
import com.thiago.desafio_estagio.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) TipoPessoa tipoPessoa,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String nome
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ClienteListDto> resultado = clienteService.listarTodos(tipoPessoa, documento, nome, pageable);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
