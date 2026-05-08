package com.thiago.desafio_estagio.relatorio.application;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final DataSource dataSource;

    // PDF e XLSX usam templates dedicados: o layout do PDF (título largo, frames, linhas
    // decorativas) confunde o detector de colunas do JRXlsxExporter. Manter dois .jrxml
    // independentes deixa cada saída otimizada para seu formato sem propriedades de exclusão.
    private final Map<FormatoRelatorio, JasperReport> templatesLista = new EnumMap<>(FormatoRelatorio.class);
    private final Map<FormatoRelatorio, JasperReport> templatesDetalhe = new EnumMap<>(FormatoRelatorio.class);

    // Compila os .jrxml uma vez no startup e mantém em memória para reutilização.
    @PostConstruct
    public void init() {
        templatesLista.put(FormatoRelatorio.PDF,
                compilar("reports/ModeloReportEstagio.jrxml", "listagem de clientes (PDF)"));
        templatesLista.put(FormatoRelatorio.XLSX,
                compilar("reports/ListagemClientesXlsx.jrxml", "listagem de clientes (XLSX)"));
        templatesDetalhe.put(FormatoRelatorio.PDF,
                compilar("reports/RelatorioClienteDetalhe.jrxml", "ficha do cliente (PDF)"));
        templatesDetalhe.put(FormatoRelatorio.XLSX,
                compilar("reports/RelatorioClienteDetalheXlsx.jrxml", "ficha do cliente (XLSX)"));
    }

    private JasperReport compilar(@lombok.NonNull String classpath, String nome) {
        ClassPathResource resource = new ClassPathResource(classpath);
        if (!resource.exists()) {
            throw new RuntimeException("Arquivo de relatório não encontrado: " + classpath);
        }
        try (InputStream stream = resource.getInputStream()) {
            return JasperCompileManager.compileReport(stream);
        } catch (JRException | IOException e) {
            throw new RuntimeException("Falha ao compilar " + nome, e);
        }
    }

    public byte[] gerarRelatorioClientes(FormatoRelatorio formato) {
        try (Connection connection = dataSource.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    templatesLista.get(formato), new HashMap<>(), connection);
            return exportar(jasperPrint, formato);
        } catch (JRException | SQLException e) {
            throw new RuntimeException("Erro ao gerar relatório de clientes", e);
        }
    }

    public byte[] gerarRelatorioClienteDetalhe(UUID clienteId, FormatoRelatorio formato) {
        try (Connection connection = dataSource.getConnection()) {
            Map<String, Object> params = new HashMap<>();
            params.put("clienteId", clienteId.toString());
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    templatesDetalhe.get(formato), params, connection);
            return exportar(jasperPrint, formato);
        } catch (JRException | SQLException e) {
            throw new RuntimeException("Erro ao gerar relatório de cliente", e);
        }
    }

    private byte[] exportar(JasperPrint jasperPrint, FormatoRelatorio formato) throws JRException {
        return switch (formato) {
            case PDF -> exportarPdf(jasperPrint);
            case XLSX -> exportarXlsx(jasperPrint);
        };
    }

    private byte[] exportarPdf(JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
        return outputStream.toByteArray();
    }

    private byte[] exportarXlsx(JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        // Configurações para deixar a planilha tabular: tudo numa única aba, sem espaços
        // vazios entre linhas/colunas, com tipagem automática (números viram número, datas
        // viram data ao invés de tudo virar texto).
        SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
        config.setOnePagePerSheet(false);
        config.setRemoveEmptySpaceBetweenRows(true);
        config.setRemoveEmptySpaceBetweenColumns(true);
        config.setDetectCellType(true);
        config.setWhitePageBackground(false);
        exporter.setConfiguration(config);

        exporter.exportReport();
        return outputStream.toByteArray();
    }
}
