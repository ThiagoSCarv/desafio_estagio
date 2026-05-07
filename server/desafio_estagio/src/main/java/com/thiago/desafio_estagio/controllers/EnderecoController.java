package com.thiago.desafio_estagio.controllers;

import com.thiago.desafio_estagio.dto.EnderecoCreateDto;
import com.thiago.desafio_estagio.dto.EnderecoDto;
import com.thiago.desafio_estagio.dto.EnderecoUpdateDto;
import com.thiago.desafio_estagio.service.EnderecoService;
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
@RequestMapping("/endereco")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoService enderecoService;

    // Cria um novo endereco associado ao cliente informado pelo path id.
    @PostMapping("/{id}")
    public ResponseEntity<EnderecoDto> criar(@PathVariable UUID id, @RequestBody @Valid EnderecoCreateDto dto) {
        EnderecoDto endereco = enderecoService.criar(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(endereco);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EnderecoDto> atualizar(@PathVariable UUID id, @RequestBody @Valid EnderecoUpdateDto dto) {
        EnderecoDto endereco = enderecoService.atualizar(id, dto);
        return ResponseEntity.ok(endereco);
    }
}
