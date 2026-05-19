package com.thiago.desafio_estagio.endereco.web.rest;

import com.thiago.desafio_estagio.endereco.application.EnderecoCreateDto;
import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
import com.thiago.desafio_estagio.endereco.application.EnderecoService;
import com.thiago.desafio_estagio.endereco.application.EnderecoUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    // Cria um novo endereco associado ao cliente informado pelo path clienteId.
    @PostMapping("/{clienteId}")
    public ResponseEntity<EnderecoDto> criar(@PathVariable UUID clienteId, @RequestBody @Valid EnderecoCreateDto dto) {
        EnderecoDto endereco = enderecoService.criar(clienteId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(endereco);
    }

    @PatchMapping("/{enderecoId}")
    public ResponseEntity<EnderecoDto> atualizar(@PathVariable UUID enderecoId, @RequestBody @Valid EnderecoUpdateDto dto) {
        EnderecoDto endereco = enderecoService.atualizar(enderecoId, dto);
        return ResponseEntity.ok(endereco);
    }

    @DeleteMapping("/{enderecoId}")
    public ResponseEntity<Void> deletar(@PathVariable UUID enderecoId) {
        enderecoService.deletar(enderecoId);
        return ResponseEntity.noContent().build();
    }
}
