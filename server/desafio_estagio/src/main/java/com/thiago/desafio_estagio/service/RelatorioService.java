package com.thiago.desafio_estagio.service;

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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final DataSource dataSource;

    private JasperReport jasperReportLista;
    private JasperReport jasperReportDetalhe;

    // Compila os .jrxml uma vez no startup e mantém em memória para reutilização
    @PostConstruct
    public void init() {
        jasperReportLista = compilar("reports/ModeloReportEstagio.jrxml", "relatório de clientes");
        jasperReportDetalhe = compilar("reports/RelatorioClienteDetalhe.jrxml", "relatório de cliente detalhe");
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

    private byte[] exportarPdf(JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
        return outputStream.toByteArray();
    }

    public byte[] gerarRelatorioClientes() {
        try (Connection connection = dataSource.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReportLista, new HashMap<>(), connection);
            return exportarPdf(jasperPrint);
        } catch (JRException | SQLException e) {
            throw new RuntimeException("Erro ao gerar relatório de clientes", e);
        }
    }

    public byte[] gerarRelatorioClienteDetalhe(UUID clienteId) {
        try (Connection connection = dataSource.getConnection()) {
            Map<String, Object> params = new HashMap<>();
            params.put("clienteId", clienteId.toString());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReportDetalhe, params, connection);
            return exportarPdf(jasperPrint);
        } catch (JRException | SQLException e) {
            throw new RuntimeException("Erro ao gerar relatório de cliente", e);
        }
    }
}
