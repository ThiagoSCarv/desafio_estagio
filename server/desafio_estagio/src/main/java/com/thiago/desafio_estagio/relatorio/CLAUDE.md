# Contexto: bounded context `relatorio`

## JasperReports

- Template: `src/main/resources/reports/ModeloReportEstagio.jrxml`
- Versão JasperReports: **6.21.5** (compatível com o template; **7.x é incompatível**).
- `RelatorioService` compila o `.jrxml` uma vez no startup via `@PostConstruct` e mantém o `JasperReport` em memória para reutilização.
- O relatório é preenchido via conexão JDBC direta ao datasource (não usa JPA).
- Exportado com `JRPdfExporter` de `net.sf.jasperreports.engine.export`.

## Colunas do relatório

Nome/Razão Social, Email, Tipo (PF/PJ), Documento (CPF ou CNPJ), Telefone, CEP, Cidade, Estado.

## Rota

| Método | Caminho | Função |
|---|---|---|
| GET | `/relatorio/clientes` | Gerar e baixar relatório PDF de clientes e endereços |
