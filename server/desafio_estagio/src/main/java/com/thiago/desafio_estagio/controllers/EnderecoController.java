package com.thiago.desafio_estagio.controllers;

import com.thiago.desafio_estagio.dto.EnderecoCreateDto;
import com.thiago.desafio_estagio.models.Cliente;
import com.thiago.desafio_estagio.models.Endereco;
import com.thiago.desafio_estagio.service.EnderecoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/endereco")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @PostMapping("/{id}")
    public ResponseEntity<?> criar(@PathVariable UUID id, @RequestBody @Valid EnderecoCreateDto dto) {
        try {
            Endereco novoEndereco = new Endereco();
            novoEndereco.setLogradouro(dto.getLogradouro());
            novoEndereco.setNumero(dto.getNumero());
            novoEndereco.setCep(dto.getCep());
            novoEndereco.setBairro(dto.getBairro());
            novoEndereco.setTelefone(dto.getTelefone());
            novoEndereco.setCidade(dto.getCidade());
            novoEndereco.setEstado(dto.getEstado());
            novoEndereco.setEnderecoPrincipal(dto.getEnderecoPrincipal());
            novoEndereco.setComplemento(dto.getComplemento());

            Cliente cliente = enderecoService.adicionarEndereco(id, novoEndereco);
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
