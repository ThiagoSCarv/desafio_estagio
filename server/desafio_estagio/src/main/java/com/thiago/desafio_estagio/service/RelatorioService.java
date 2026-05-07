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

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final DataSource dataSource;

    private JasperReport jasperReport;

    // Compila o .jrxml uma vez no startup e mantém em memória para reutilização
    @PostConstruct
    public void init() {
        ClassPathResource resource = new ClassPathResource("reports/ModeloReportEstagio.jrxml");
        if (!resource.exists()) {
            throw new RuntimeException("Arquivo de relatório não encontrado no classpath: reports/ModeloReportEstagio.jrxml");
        }
        try (InputStream stream = resource.getInputStream()) {
            jasperReport = JasperCompileManager.compileReport(stream);
        } catch (JRException | IOException e) {
            throw new RuntimeException("Falha ao compilar relatório de clientes", e);
        }
    }

    public byte[] gerarRelatorioClientes() {
        try (Connection connection = dataSource.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), connection);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.exportReport();

            return outputStream.toByteArray();
        } catch (JRException | SQLException e) {
            throw new RuntimeException("Erro ao gerar relatório de clientes", e);
        }
    }
}
