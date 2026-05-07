# Desafio Estágio

Backend Spring Boot 3 / Java para CRUD de Cliente (PF/PJ) e Endereço com geração de relatório PDF.

## Stack

- Spring Boot 3, Spring Data JPA, Bean Validation
- MySQL (`ddl-auto=update` via `application.properties`)
- Lombok, Java records
- JasperReports 6.21.5 + iText 4.2.2 (relatório PDF)

## Modelo de domínio

### Cliente (herança JOINED)
- `Cliente` (abstract): `id`, `tipoPessoa`, `email` (unique), `ativo`, `criadoEm`, `atualizadoEm`
- `ClientePf extends Cliente`: `nome`, `cpf` (unique, 11 dígitos), `rg`, `dataNascimento`
- `ClientePj extends Cliente`: `cnpj` (unique, 14 dígitos), `razaoSocial` (unique), `inscricaoEstadual`, `dataCriacao`

### Endereço
Relação **unidirecional N:1** com Cliente — `Endereco.cliente` aponta via FK `cliente_id`. `Cliente` **não** mantém `List<Endereco>`.

Para listar endereços de um cliente: `EnderecoRepository.findByClienteId(UUID)`.

`@OnDelete(CASCADE)` na FK garante remoção em cascata via banco.

## Convenções

### Arquitetura
- **Controllers** não usam `try/catch`. Erros são tratados em `ExceptionHandlerController` via `@RestControllerAdvice`.
- **Services** retornam DTOs, nunca entidades. Cada DTO de resposta tem fábrica estática `from(Entity)`.
- **DI** sempre por construtor (`@RequiredArgsConstructor` + `private final`).
- **Leituras** marcadas com `@Transactional(readOnly = true)`.

### DTOs
- **Resposta**: records polimórfos via sealed interface — `ClienteDto` (sealed) permits `ClientePfDto`, `ClientePjDto`. Ambos incluem `List<EnderecoDto> enderecos`.
- **Entrada**: records com Bean Validation (`@NotBlank`, `@Email`, `@ValidCpf`, `@ValidCnpj`, `@ValidRg`, `@ValidCep`, `@ValidTelefone`).

### Rotas
| Método | Caminho | Função |
|---|---|---|
| GET | `/clientes` | Listar com filtros (`tipoPessoa`, `documento`, `nome`) + paginação |
| GET | `/clientes/{id}` | Detalhe com endereços |
| DELETE | `/clientes/{id}` | Remover (cascata em endereços) |
| POST / PATCH | `/clientes/pf` `/clientes/pf/{id}` | PF |
| POST / PATCH | `/clientes/pj` `/clientes/pj/{id}` | PJ |
| POST | `/endereco/{clienteId}` | Criar endereço |
| PATCH | `/endereco/{id}` | Atualizar endereço (numero, telefone, complemento, enderecoPrincipal) |
| DELETE | `/endereco/{id}` | Deletar endereço |
| GET | `/relatorio/clientes` | Gerar e baixar relatório PDF de clientes e endereços |

### Normalização de documentos
- **CPF / CNPJ**: persistidos apenas com dígitos. Services normalizam via `replaceAll("[^0-9]", "")` antes de checar duplicidade e salvar.
- **RG**: persistido alfanumérico maiúsculo (`replaceAll("[.\\-\\s]", "").toUpperCase()`).

### Unicidade de email
Sempre via `ClienteRepository.existsByEmail` (tabela pai) — cobre PF e PJ. Não usar `existsByEmail` em sub-repositórios.

### Endereço principal
Apenas **um** endereço pode ser principal por cliente. `EnderecoService.criar` e `EnderecoService.atualizar` chamam `enderecoRepository.desmarcarTodosPrincipaisDoCliente` antes de gravar quando o endereço se torna principal.

### Campos editáveis via PATCH /endereco/{id}
Apenas: `numero`, `telefone`, `complemento`, `enderecoPrincipal`. Campos de localização (`cep`, `logradouro`, `bairro`, `cidade`, `estado`) são imutáveis após criação.

### Relatório PDF (JasperReports)
- Template: `src/main/resources/reports/ModeloReportEstagio.jrxml`
- Versão JasperReports: **6.21.5** (compatível com o template; 7.x é incompatível).
- `RelatorioService` compila o `.jrxml` uma vez no startup via `@PostConstruct` e mantém o `JasperReport` em memória para reutilização.
- O relatório é preenchido via conexão JDBC direta ao datasource (não usa JPA).
- Exportado com `JRPdfExporter` de `net.sf.jasperreports.engine.export`.
- Colunas do relatório: Nome/Razão Social, Email, Tipo (PF/PJ), Documento (CPF ou CNPJ), Telefone, CEP, Cidade, Estado.
- Dados de exemplo para teste em `src/main/resources/db/seed.sql`.

### Estilo de código
- Comentários explicativos em **português** em classes/métodos/blocos não triviais (override da regra default de "não comentar").
- Nomes em **inglês** para locais/métodos; **português** para campos de domínio amarrados ao schema (`nome`, `email`, `tipoPessoa`).

### Commits
Mensagens de uma linha, em estilo gerundivo pt-BR. Sem corpo, sem `Co-Authored-By`.

Exemplos: `feat: listando clientes`, `refactor: ajustando schemas de ClientePf e ClientePj`.

## Pendências operacionais (DB)

`ddl-auto=update` não aplica algumas mudanças retroativamente. Antes do próximo deploy, executar manualmente:

```sql
-- razao_social agora é unique
ALTER TABLE clientes_pj ADD CONSTRAINT uk_clientes_pj_razao_social UNIQUE (razao_social);

-- typo: inscricao_estatual -> inscricao_estadual (preserva dados)
ALTER TABLE clientes_pj CHANGE inscricao_estatual inscricao_estadual VARCHAR(255);

-- CPF/CNPJ sem máscara em dados existentes
UPDATE clientes_pf SET cpf = REGEXP_REPLACE(cpf, '[^0-9]', '');
UPDATE clientes_pj SET cnpj = REGEXP_REPLACE(cnpj, '[^0-9]', '');
ALTER TABLE clientes_pf MODIFY cpf VARCHAR(11) NOT NULL;
ALTER TABLE clientes_pj MODIFY cnpj VARCHAR(14) NOT NULL;

-- coluna posicao do antigo @OrderColumn ficou órfã após remoção da List<Endereco>
ALTER TABLE enderecos DROP COLUMN posicao;

-- adicionar ON DELETE CASCADE na FK cliente_id (caso não tenha sido recriada)
-- verifique o nome real da constraint antes de executar
ALTER TABLE enderecos DROP FOREIGN KEY <fk_name>;
ALTER TABLE enderecos
  ADD CONSTRAINT fk_enderecos_cliente FOREIGN KEY (cliente_id)
  REFERENCES clientes(id) ON DELETE CASCADE;
```

## Histórico da revisão sênior

Refatoração ampla aplicada (commits `793dce4..f7109df` em main). Tópicos cobertos:

### Bugs críticos
1. `ClienteService.buscarPorId` ausente — implementado consultando endereços via `EnderecoRepository`.
2. `try/catch (Exception)` nos controllers anulava o `@RestControllerAdvice` — removido. Agora erros propagam ao handler global e devolvem o status correto (404, 409, 422).
3. `EnderecoController` chamava método errado e pulava validação de CEP duplicado — passou a chamar `EnderecoService.criar`.
4. Lógica de "endereço principal" desmarcava só o primeiro item — agora desmarca todos via query `UPDATE`.

### Refatorações de design
- Relação `Cliente -> Endereco` mudou de bidirecional com `List<Endereco>` para unidirecional N:1.
- DTOs anêmicos (campos sempre `null` para o tipo "errado") substituídos por sealed interface + records.
- DTOs de entrada convertidos para records.
- Field injection (`@Autowired` em campo) → constructor injection com `@RequiredArgsConstructor`.
- Validação de email cruza PF e PJ via `ClienteRepository`.
- `razao_social` ganhou `unique=true`.
- Mapeamento `/clientes` separado em `/clientes/pf` e `/clientes/pj` (sem 3 controllers competindo pelo mesmo prefixo).
- API devolve DTOs em todas as respostas — entidades nunca vazam.
- `GET /clientes/{id}` passou a retornar a lista de endereços do cliente.

### Melhorias
- CPF/CNPJ armazenados apenas com dígitos.
- `ClientePjUpdateDto` permite atualizar `razaoSocial` e `inscricaoEstadual`.
- Typo `inscricaoEstatual` → `inscricaoEstadual` corrigido.
- `PATCH /endereco/{id}` e `DELETE /endereco/{id}` adicionados.
- Relatório PDF via JasperReports adicionado (`GET /relatorio/clientes`).
- Seed de dados de exemplo em `src/main/resources/db/seed.sql`.

## Layout

```
server/desafio_estagio/src/main/java/com/thiago/desafio_estagio/
├── controllers/
│   ├── ClienteController.java        # GET /clientes, GET /clientes/{id}, DELETE /clientes/{id}
│   ├── ClientePfController.java      # POST /clientes/pf, PATCH /clientes/pf/{id}
│   ├── ClientePjController.java      # POST /clientes/pj, PATCH /clientes/pj/{id}
│   ├── EnderecoController.java       # POST, PATCH, DELETE /endereco/{id}
│   └── RelatorioController.java      # GET /relatorio/clientes
├── dto/
│   ├── ClienteDto.java               # sealed interface com List<EnderecoDto> enderecos()
│   ├── ClientePfDto.java             # record PF com enderecos
│   ├── ClientePjDto.java             # record PJ com enderecos
│   ├── EnderecoDto.java
│   ├── ClientePfCreateDto.java
│   ├── ClientePfUpdateDto.java
│   ├── ClientePjCreateDto.java
│   ├── ClientePjUpdateDto.java
│   ├── EnderecoCreateDto.java
│   └── EnderecoUpdateDto.java        # numero, telefone, complemento, enderecoPrincipal
├── exceptions/
│   ├── ExceptionHandlerController.java
│   ├── ClienteNaoEncontradoException.java
│   ├── EnderecoNaoEncontradoException.java
│   ├── CepJaCadastradoException.java
│   ├── CpfJaCadastradoException.java
│   ├── CnpjJaCadastradoException.java
│   ├── EmailJaCadastradoException.java
│   ├── RazaoSocialJaCadastradaException.java
│   ├── RgJaCadastradoException.java
│   └── ErrorMessageDTO.java
├── models/
│   ├── Cliente.java                  # abstract, JOINED inheritance
│   ├── ClientePf.java
│   ├── ClientePj.java
│   ├── Endereco.java
│   └── TipoPessoa.java               # enum: FISICA, JURIDICA
├── repository/
│   ├── ClienteRepository.java
│   ├── ClientePfRepository.java
│   ├── ClientePjRepository.java
│   ├── EnderecoRepository.java
│   └── ClienteSpecification.java
├── service/
│   ├── ClienteService.java
│   ├── ClientePfService.java
│   ├── ClientePjService.java
│   ├── EnderecoService.java          # criar, atualizar, deletar
│   └── RelatorioService.java         # compila JRXML no startup, gera PDF via JDBC
└── validation/
    ├── annotation/                   # @ValidCpf, @ValidCnpj, @ValidRg, @ValidCep, @ValidTelefone
    └── validator/                    # implementações dos validators

server/desafio_estagio/src/main/resources/
├── application.properties
├── db/
│   └── seed.sql                      # dados de exemplo (UUIDs via UUID_TO_BIN, 3 PF + 2 PJ)
└── reports/
    └── ModeloReportEstagio.jrxml     # template JasperReports 6.21.5
```
