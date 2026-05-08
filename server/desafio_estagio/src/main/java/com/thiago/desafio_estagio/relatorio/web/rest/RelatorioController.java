package com.thiago.desafio_estagio.relatorio.web.rest;

import com.thiago.desafio_estagio.relatorio.application.FormatoRelatorio;
import com.thiago.desafio_estagio.relatorio.application.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/relatorio")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    // Gera o relatório consolidado de clientes. Por padrão entrega PDF; aceita
    // ?formato=xlsx para baixar a mesma listagem como planilha Excel.
    @GetMapping("/clientes")
    public ResponseEntity<byte[]> relatorioClientes(
            @RequestParam(defaultValue = "pdf") String formato) {
        FormatoRelatorio fmt = FormatoRelatorio.from(formato);
        byte[] body = relatorioService.gerarRelatorioClientes(fmt);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio_clientes." + fmt.extensao())
                .contentType(fmt.mediaType())
                .body(body);
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<byte[]> relatorioClienteDetalhe(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "pdf") String formato) {
        FormatoRelatorio fmt = FormatoRelatorio.from(formato);
        byte[] body = relatorioService.gerarRelatorioClienteDetalhe(id, fmt);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio_cliente." + fmt.extensao())
                .contentType(fmt.mediaType())
                .body(body);
    }
}
