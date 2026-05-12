# Desafio Estágio

Backend Spring Boot 3 / Java para CRUD de Cliente (PF/PJ) e Endereço com geração de relatório PDF.

## Stack

- Spring Boot 3, Spring Data JPA, Bean Validation
- MySQL (`ddl-auto=update` via `application.properties`)
- Lombok, Java records
- JasperReports 6.21.5 + iText 4.2.2 (relatório PDF)

## Estrutura de pacotes

```text
desafio_estagio/
├── cliente/
│   ├── application/   # DTOs + Services
│   ├── domain/        # Entidades + Repositories + Exceptions
│   └── web/rest/      # Controllers
├── endereco/
│   ├── application/   # DTOs + EnderecoService
│   ├── domain/        # Endereco + EnderecoRepository + Exceptions
│   └── web/rest/      # EnderecoController
├── relatorio/
│   ├── application/   # RelatorioService + FormatoRelatorio + Exception
│   └── web/rest/      # RelatorioController
└── shared/
    ├── exceptions/    # ExceptionHandlerController + ErrorMessageDTO + ErrorResponse
    └── validation/    # annotation/ + validator/
```

Cada bounded context tem seu próprio `CLAUDE.md` com detalhes específicos.

## Convenções globais

### Arquitetura

- **Controllers** não usam `try/catch`. Erros propagam para `ExceptionHandlerController` via `@RestControllerAdvice`.
- **Services** retornam DTOs, nunca entidades. Cada DTO de resposta tem fábrica estática `from(Entity)`.
- **DI** sempre por construtor (`@RequiredArgsConstructor` + `private final`).
- **Leituras** marcadas com `@Transactional(readOnly = true)`.

### Normalização de documentos

- **CPF / CNPJ**: persistidos apenas com dígitos. Services normalizam via `replaceAll("[^0-9]", "")` antes de checar duplicidade e salvar.
- **RG**: persistido alfanumérico maiúsculo (`replaceAll("[.\\-\\s]", "").toUpperCase()`).

### Estilo de código

- Comentários explicativos em **português** em classes/métodos/blocos não triviais.
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

Refatoração ampla aplicada (commits `793dce4..f7109df` em main).

### Bugs críticos corrigidos

1. `ClienteService.buscarPorId` ausente — implementado consultando endereços via `EnderecoRepository`.
2. `try/catch (Exception)` nos controllers anulava o `@RestControllerAdvice` — removido.
3. `EnderecoController` chamava método errado e pulava validação de CEP duplicado.
4. Lógica de "endereço principal" desmarcava só o primeiro item — agora desmarca todos via `UPDATE`.

### Refatorações de design

- Relação `Cliente -> Endereco` mudou de bidirecional com `List<Endereco>` para unidirecional N:1.
- DTOs anêmicos substituídos por sealed interface + records.
- Field injection (`@Autowired`) → constructor injection com `@RequiredArgsConstructor`.
- `ClienteSpecification` (Criteria API com `cb.treat()`) substituído por DAO via `ClienteRepositoryCustomImpl` (JPQL com `TYPE` + `TREAT`).
- API devolve DTOs em todas as respostas — entidades nunca vazam.

### Melhorias

- CPF/CNPJ armazenados apenas com dígitos.
- `PATCH /endereco/{id}` e `DELETE /endereco/{id}` adicionados.
- Relatório PDF via JasperReports adicionado (`GET /relatorio/clientes`).
- Seed de dados de exemplo em `src/main/resources/db/seed.sql`.
