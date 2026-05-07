package com.thiago.desafio_estagio.controllers;

import com.thiago.desafio_estagio.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/relatorio")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/clientes")
    public ResponseEntity<byte[]> relatorioClientes() {
        byte[] pdf = relatorioService.gerarRelatorioClientes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_clientes.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<byte[]> relatorioClienteDetalhe(@PathVariable UUID id) {
        byte[] pdf = relatorioService.gerarRelatorioClienteDetalhe(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_cliente.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
