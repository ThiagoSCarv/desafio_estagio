package com.thiago.desafio_estagio.cliente.application;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportacaoService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ClientePfService clientePfService;
    private final ClientePjService clientePjService;

    public ImportacaoResultado importarClientes(InputStream xlsx) throws IOException {
        int criados = 0;
        List<String> erros = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(xlsx)) {
            Sheet sheet = workbook.getSheetAt(0);
            // linha 0 é cabeçalho — começa da linha 1
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || estaVazia(row)) continue;

                try {
                    String tipo = celula(row, 0).trim().toUpperCase();
                    if ("PF".equals(tipo)) {
                        clientePfService.criar(lerPf(row));
                    } else if ("PJ".equals(tipo)) {
                        clientePjService.criar(lerPj(row));
                    } else {
                        erros.add("Linha " + (i + 1) + ": tipo inválido \"" + tipo + "\" (esperado PF ou PJ)");
                        continue;
                    }
                    criados++;
                } catch (Exception e) {
                    erros.add("Linha " + (i + 1) + ": " + e.getMessage());
                }
            }
        }

        return new ImportacaoResultado(criados, erros);
    }

    public void escreverTemplate(OutputStream out) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clientes");

            CellStyle cabecalhoStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            cabecalhoStyle.setFont(boldFont);

            String[] cabecalhos = {"tipo", "email", "cpf_cnpj", "nome_razaoSocial", "rg_inscricaoEstadual", "data"};
            Row cabecalho = sheet.createRow(0);
            for (int i = 0; i < cabecalhos.length; i++) {
                Cell cell = cabecalho.createCell(i);
                cell.setCellValue(cabecalhos[i]);
                cell.setCellStyle(cabecalhoStyle);
            }

            Object[][] exemplos = {
                {"PF", "joao@exemplo.com", "12345678909", "João Silva",    "MG1234567",    "01/01/1990"},
                {"PJ", "emp@exemplo.com",  "11222333000181", "Empresa LTDA", "123456789", "01/01/2010"}
            };
            for (int r = 0; r < exemplos.length; r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < exemplos[r].length; c++) {
                    row.createCell(c).setCellValue((String) exemplos[r][c]);
                }
            }

            for (int i = 0; i < cabecalhos.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        }
    }

    private ClientePfCreateDto lerPf(Row row) {
        String email          = celula(row, 1);
        String cpf            = celula(row, 2);
        String nome           = celula(row, 3);
        String rg             = celula(row, 4);
        LocalDate nascimento  = parseData(row, 5);
        return new ClientePfCreateDto(email, nome, cpf, rg, nascimento, List.of());
    }

    private ClientePjCreateDto lerPj(Row row) {
        String email              = celula(row, 1);
        String cnpj               = celula(row, 2);
        String razaoSocial        = celula(row, 3);
        String inscricaoEstadual  = celula(row, 4);
        LocalDate dataCriacao     = parseData(row, 5);
        return new ClientePjCreateDto(email, cnpj, razaoSocial, inscricaoEstadual, dataCriacao, List.of());
    }

    private String celula(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().format(FORMATO_DATA);
                }
                // números como CPF/CNPJ armazenados como numeric — preserva como inteiro
                yield String.valueOf((long) cell.getNumericCellValue());
            }
            default -> "";
        };
    }

    private LocalDate parseData(Row row, int col) {
        String valor = celula(row, col);
        try {
            return LocalDate.parse(valor, FORMATO_DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("data inválida \"" + valor + "\" (esperado dd/MM/yyyy)");
        }
    }

    private boolean estaVazia(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().isBlank()) {
                return false;
            }
        }
        return true;
    }
}
