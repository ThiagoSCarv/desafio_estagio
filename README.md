# Desafio Estágio

API REST + interface web para CRUD de clientes (Pessoa Física e Jurídica) com gestão de endereços, geração de relatórios e importação em lote via planilha.

## Stack

| Camada | Tecnologia |
|---|---|
| Framework | Spring Boot 3.5 / Java 17 |
| Persistência | Spring Data JPA + MySQL 8 |
| Interface web | Apache Wicket 10 |
| Relatórios | JasperReports 6.21.5 + iText 4.2.2 |
| Importação/Exportação | Apache POI (XLSX) |
| Validação | Bean Validation (Jakarta) |
| Utilitários | Lombok |
| Testes | JUnit 5 + H2 (in-memory) |

## Pré-requisitos

- Java 17+
- Maven 3.9+
- Docker (para subir o banco)

## Como rodar

**1. Suba o banco de dados**

```bash
cd server/desafio_estagio
docker compose up -d
```

O MySQL fica disponível em `localhost:3307` com banco `desafio_estagio`, usuário `admin` e senha `admin`.

**2. Execute a aplicação**

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

**3. (Opcional) Populando dados de exemplo**

Execute o script `src/main/resources/db/seed.sql` no banco após a primeira inicialização.

## Interface web

A aplicação inclui uma interface web construída com Apache Wicket, acessível em `http://localhost:8080`.

- **Home (`/`)** — lista paginada de clientes com filtro em tempo real por tipo, documento e nome/razão social. Permite criar, editar e excluir clientes, e baixar relatório PDF.
- **Detalhes (`/detalhes/{id}`)** — exibe dados completos do cliente e lista de endereços, com modais para adicionar, editar e excluir endereços.

## API REST

### Clientes

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/clientes` | Lista clientes com filtros e paginação |
| `GET` | `/clientes/{id}` | Retorna cliente por ID (inclui endereços) |
| `POST` | `/clientes/pf` | Cria cliente Pessoa Física |
| `PATCH` | `/clientes/pf/{id}` | Atualiza dados de Pessoa Física |
| `POST` | `/clientes/pj` | Cria cliente Pessoa Jurídica |
| `PATCH` | `/clientes/pj/{id}` | Atualiza dados de Pessoa Jurídica |
| `DELETE` | `/clientes/{id}` | Remove cliente (cascata em endereços) |

**Parâmetros de listagem (`GET /clientes`):**

| Parâmetro | Tipo | Padrão | Descrição |
|---|---|---|---|
| `page` | int | 0 | Página |
| `size` | int | 10 | Itens por página |
| `tipoPessoa` | `PF` \| `PJ` | — | Filtro por tipo |
| `documento` | string | — | CPF (PF) ou CNPJ (PJ) |
| `nome` | string | — | Nome (PF) ou razão social (PJ) |

### Endereços

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/endereco/{clienteId}` | Cria endereço para o cliente |
| `PATCH` | `/endereco/{id}` | Atualiza número, telefone, complemento ou flag de principal |
| `DELETE` | `/endereco/{id}` | Remove endereço |

> Campos de localização (CEP, logradouro, bairro, cidade, estado) são imutáveis após a criação. Apenas um endereço pode ser marcado como principal por cliente.

### Relatórios

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/relatorio/clientes` | Relatório consolidado de clientes (PDF ou XLSX) |
| `GET` | `/relatorio/clientes/{id}` | Relatório de detalhe de um cliente (PDF ou XLSX) |

Passe `?formato=xlsx` para receber planilha Excel em vez de PDF.

### Importação em lote

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/clientes/importar/template` | Baixa planilha modelo (.xlsx) |

O template contém as colunas: `tipo`, `email`, `cpf_cnpj`, `nome_razaoSocial`, `rg_inscricaoEstadual`, `data`. Preencha com `PF` ou `PJ` na coluna `tipo` e envie o arquivo preenchido para a rota de importação.

## Validações

O projeto inclui validators customizados com Bean Validation:

- `@ValidCpf` — dígitos verificadores do CPF
- `@ValidCnpj` — dígitos verificadores do CNPJ
- `@ValidRg` — formato alfanumérico de RG
- `@ValidCep` — formato de CEP
- `@ValidTelefone` — formato de telefone brasileiro

CPF e CNPJ são persistidos apenas com dígitos (máscaras removidas antes de salvar).

## Estrutura de pacotes

```
desafio_estagio/
├── cliente/
│   ├── application/   # DTOs, ClienteService, ClientePfService, ClientePjService, ImportacaoService
│   ├── domain/        # Cliente (abstract), ClientePf, ClientePj, repositórios, TipoPessoa
│   └── web/rest/      # ClienteController, ClientePfController, ClientePjController, ImportacaoController
├── endereco/
│   ├── application/   # DTOs, EnderecoService
│   ├── domain/        # Endereco, EnderecoRepository
│   └── web/rest/      # EnderecoController
├── relatorio/
│   ├── application/   # RelatorioService, FormatoRelatorio
│   └── web/rest/      # RelatorioController
└── shared/
    ├── exceptions/    # ExceptionHandlerController (@RestControllerAdvice), ErrorResponse
    ├── validation/    # Annotations e validators customizados
    └── wicket/        # Páginas, componentes e configuração do Wicket
```

## Modelo de domínio

`Cliente` é uma entidade abstrata com herança **JOINED** (`@Inheritance(strategy = JOINED)`):

- `ClientePf` — campos: `nome`, `cpf` (unique, 11 dígitos), `rg`, `dataNascimento`
- `ClientePj` — campos: `cnpj` (unique, 14 dígitos), `razaoSocial` (unique), `inscricaoEstadual`, `dataCriacao`

`Endereco` tem relação N:1 com `Cliente` via FK `cliente_id` com `ON DELETE CASCADE`.

## Testes

```bash
./mvnw test
```

Os testes de unidade rodam com H2 em memória. Há também testes de integração (`ClienteIT`, `EnderecoIT`) e testes de controller com MockMvc.

## Coleção de requisições

O arquivo `Insomnia_2026-05-07.yaml` na raiz do projeto contém todas as requisições prontas para importar no Insomnia.
