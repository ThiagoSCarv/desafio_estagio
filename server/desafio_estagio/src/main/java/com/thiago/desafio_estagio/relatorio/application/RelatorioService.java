package com.thiago.desafio_estagio.relatorio.application;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.thiago.desafio_estagio.relatorio.application.exceptions.RelatorioException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final DataSource dataSource;

    private JasperReport templateListaPdf;
    private JasperReport templateDetalhePdf;

    // Compila os .jrxml uma vez no startup e mantém em memória para reutilização.
    @PostConstruct
    public void init() {
        templateListaPdf   = compilar("reports/ModeloReportEstagio.jrxml",     "listagem de clientes (PDF)");
        templateDetalhePdf = compilar("reports/RelatorioClienteDetalhe.jrxml", "ficha do cliente (PDF)");
    }

    private JasperReport compilar(@lombok.NonNull String classpath, String nome) {
        ClassPathResource resource = new ClassPathResource(classpath);
        if (!resource.exists()) {
            throw new RelatorioException("Arquivo de relatório não encontrado: " + classpath);
        }
        try (InputStream stream = resource.getInputStream()) {
            return JasperCompileManager.compileReport(stream);
        } catch (JRException | IOException e) {
            throw new RelatorioException("Falha ao compilar " + nome, e);
        }
    }

    public byte[] gerarRelatorioClientes(FormatoRelatorio formato) {
        return switch (formato) {
            case PDF  -> gerarPdfClientes();
            case XLSX -> gerarXlsxClientes();
        };
    }

    public byte[] gerarRelatorioClienteDetalhe(UUID clienteId, FormatoRelatorio formato) {
        return switch (formato) {
            case PDF  -> gerarPdfClienteDetalhe(clienteId);
            case XLSX -> gerarXlsxClienteDetalhe(clienteId);
        };
    }

    private byte[] gerarPdfClientes() {
        try (Connection conn = dataSource.getConnection()) {
            JasperPrint print = JasperFillManager.fillReport(templateListaPdf, new HashMap<>(), conn);
            return exportarPdf(print);
        } catch (JRException | SQLException e) {
            throw new RelatorioException("Erro ao gerar relatório de clientes", e);
        }
    }

    private byte[] gerarPdfClienteDetalhe(UUID clienteId) {
        try (Connection conn = dataSource.getConnection()) {
            Map<String, Object> params = new HashMap<>();
            params.put("clienteId", clienteId.toString());
            JasperPrint print = JasperFillManager.fillReport(templateDetalhePdf, params, conn);
            return exportarPdf(print);
        } catch (JRException | SQLException e) {
            throw new RelatorioException("Erro ao gerar relatório de cliente", e);
        }
    }

    private byte[] exportarPdf(JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
        return outputStream.toByteArray();
    }

    private byte[] gerarXlsxClientes() {
        String sql = """
                SELECT
                    c.email, c.tipo_pessoa,
                    COALESCE(pf.nome, pj.razao_social) AS nome_razao,
                    COALESCE(pf.cpf, pj.cnpj)          AS documento,
                    e.telefone, e.cep, e.cidade, e.estado
                FROM desafio_estagio.clientes c
                INNER JOIN desafio_estagio.enderecos    e  ON c.id = e.cliente_id
                LEFT JOIN  desafio_estagio.clientes_pf pf ON c.id = pf.id
                LEFT JOIN  desafio_estagio.clientes_pj pj ON c.id = pj.id
                """;

        try (Connection conn           = dataSource.getConnection();
             PreparedStatement ps      = conn.prepareStatement(sql);
             ResultSet rs              = ps.executeQuery();
             XSSFWorkbook workbook     = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Clientes");

            CellStyle estiloCabecalho = cabecalhoStyle(workbook);
            String[] colunas = {"Nome / Razão Social", "Email", "Tipo", "Documento",
                                "Telefone", "CEP", "Cidade", "Estado"};
            Row linhaCabecalho = sheet.createRow(0);
            for (int i = 0; i < colunas.length; i++) {
                Cell celula = linhaCabecalho.createCell(i);
                celula.setCellValue(colunas[i]);
                celula.setCellStyle(estiloCabecalho);
            }

            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rs.getString("nome_razao"));
                row.createCell(1).setCellValue(rs.getString("email"));
                row.createCell(2).setCellValue(rs.getString("tipo_pessoa"));
                row.createCell(3).setCellValue(rs.getString("documento"));
                row.createCell(4).setCellValue(rs.getString("telefone"));
                row.createCell(5).setCellValue(rs.getString("cep"));
                row.createCell(6).setCellValue(rs.getString("cidade"));
                row.createCell(7).setCellValue(rs.getString("estado"));
            }

            for (int i = 0; i < colunas.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
            return out.toByteArray();
        } catch (SQLException | IOException e) {
            throw new RelatorioException("Erro ao gerar relatório XLSX de clientes", e);
        }
    }

    private byte[] gerarXlsxClienteDetalhe(UUID clienteId) {
        String sql = """
                SELECT
                    c.email, c.tipo_pessoa,
                    CASE WHEN c.ativo = 1 THEN 'Sim' ELSE 'Não' END AS ativo,
                    COALESCE(pf.nome, pj.razao_social) AS nome_razao,
                    COALESCE(pf.cpf,  pj.cnpj)         AS documento,
                    pf.rg, pf.data_nascimento,
                    pj.inscricao_estadual, pj.data_criacao,
                    e.logradouro, e.numero, e.complemento, e.bairro,
                    e.cep, e.telefone, e.cidade, e.estado,
                    CASE WHEN e.endereco_principal = 1 THEN 'Sim' ELSE 'Não' END AS principal
                FROM desafio_estagio.clientes c
                LEFT JOIN desafio_estagio.clientes_pf pf ON c.id = pf.id
                LEFT JOIN desafio_estagio.clientes_pj pj ON c.id = pj.id
                LEFT JOIN desafio_estagio.enderecos   e  ON c.id = e.cliente_id
                WHERE c.id = UUID_TO_BIN(?)
                """;

        try (Connection conn           = dataSource.getConnection();
             PreparedStatement ps      = conn.prepareStatement(sql);
             XSSFWorkbook workbook     = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            ps.setString(1, clienteId.toString());
            ResultSet rs = ps.executeQuery();

            Sheet sheet               = workbook.createSheet("Ficha do Cliente");
            CellStyle estiloCabecalho = cabecalhoStyle(workbook);
            CellStyle estiloSecao     = secaoStyle(workbook);
            String[] colunasEndereco  = {"Logradouro", "Número", "Complemento", "Bairro",
                                         "CEP", "Telefone", "Cidade", "Estado", "Principal"};
            int rowNum = 0;

            if (rs.next()) {
                rowNum = escreverSecaoCliente(sheet, rs, rowNum, estiloCabecalho, estiloSecao);
                rowNum = escreverCabecalhoEnderecos(sheet, rowNum, estiloCabecalho, estiloSecao, colunasEndereco);
                do {
                    if (rs.getString("logradouro") != null) {
                        escreverLinhaEndereco(sheet, rowNum++, rs);
                    }
                } while (rs.next());
            }
            rs.close();

            for (int i = 0; i < colunasEndereco.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
            return out.toByteArray();
        } catch (SQLException | IOException e) {
            throw new RelatorioException("Erro ao gerar relatório XLSX de cliente", e);
        }
    }

    private int escreverSecaoCliente(Sheet sheet, ResultSet rs, int rowNum,
                                      CellStyle estiloCabecalho, CellStyle estiloSecao) throws SQLException {
        escreverSecao(sheet, rowNum++, estiloSecao, "DADOS DO CLIENTE");
        escreverLabel(sheet, rowNum++, estiloCabecalho, "Nome / Razão Social",  rs.getString("nome_razao"));
        escreverLabel(sheet, rowNum++, estiloCabecalho, "Tipo de Pessoa",        rs.getString("tipo_pessoa"));
        escreverLabel(sheet, rowNum++, estiloCabecalho, "E-mail",               rs.getString("email"));
        escreverLabel(sheet, rowNum++, estiloCabecalho, "Ativo",                rs.getString("ativo"));
        escreverLabel(sheet, rowNum++, estiloCabecalho, "Documento (CPF/CNPJ)", rs.getString("documento"));
        escreverLabel(sheet, rowNum++, estiloCabecalho, "RG",                   rs.getString("rg"));
        escreverLabel(sheet, rowNum++, estiloCabecalho, "Data de Nascimento",   Objects.toString(rs.getDate("data_nascimento"), ""));
        escreverLabel(sheet, rowNum++, estiloCabecalho, "Inscrição Estadual",   rs.getString("inscricao_estadual"));
        escreverLabel(sheet, rowNum++, estiloCabecalho, "Data de Criação",      Objects.toString(rs.getDate("data_criacao"), ""));
        return rowNum;
    }

    private int escreverCabecalhoEnderecos(Sheet sheet, int rowNum, CellStyle estiloCabecalho,
                                            CellStyle estiloSecao, String[] colunasEndereco) {
        rowNum++; // linha em branco
        escreverSecao(sheet, rowNum++, estiloSecao, "ENDEREÇOS");
        Row linhaCabecalhoEnderecos = sheet.createRow(rowNum++);
        for (int i = 0; i < colunasEndereco.length; i++) {
            Cell celula = linhaCabecalhoEnderecos.createCell(i);
            celula.setCellValue(colunasEndereco[i]);
            celula.setCellStyle(estiloCabecalho);
        }
        return rowNum;
    }

    private void escreverLinhaEndereco(Sheet sheet, int rowNum, ResultSet rs) throws SQLException {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(rs.getString("logradouro"));
        row.createCell(1).setCellValue(rs.getString("numero"));
        row.createCell(2).setCellValue(Objects.toString(rs.getString("complemento"), ""));
        row.createCell(3).setCellValue(rs.getString("bairro"));
        row.createCell(4).setCellValue(rs.getString("cep"));
        row.createCell(5).setCellValue(rs.getString("telefone"));
        row.createCell(6).setCellValue(rs.getString("cidade"));
        row.createCell(7).setCellValue(rs.getString("estado"));
        row.createCell(8).setCellValue(rs.getString("principal"));
    }

    private CellStyle cabecalhoStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle secaoStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private void escreverLabel(Sheet sheet, int rowNum, CellStyle labelStyle, String label, String valor) {
        Row row = sheet.createRow(rowNum);
        Cell c  = row.createCell(0);
        c.setCellValue(label);
        c.setCellStyle(labelStyle);
        row.createCell(1).setCellValue(Objects.toString(valor, ""));
    }

    private void escreverSecao(Sheet sheet, int rowNum, CellStyle style, String titulo) {
        Row row = sheet.createRow(rowNum);
        Cell c  = row.createCell(0);
        c.setCellValue(titulo);
        c.setCellStyle(style);
    }
}
