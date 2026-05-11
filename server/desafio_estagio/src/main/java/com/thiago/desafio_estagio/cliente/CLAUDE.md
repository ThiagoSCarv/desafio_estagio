# Contexto: bounded context `cliente`

## Modelo de domínio

Herança **JOINED** — cada subtipo tem sua própria tabela.

| Classe | Tabela | Campos próprios |
|---|---|---|
| `Cliente` (abstract) | `clientes` | `id`, `tipoPessoa`, `email` (unique), `ativo`, `criadoEm`, `atualizadoEm` |
| `ClientePf` | `clientes_pf` | `nome`, `cpf` (unique, 11 dígitos), `rg`, `dataNascimento` |
| `ClientePj` | `clientes_pj` | `cnpj` (unique, 14 dígitos), `razaoSocial` (unique), `inscricaoEstadual`, `dataCriacao` |

## Repositórios

### Busca com filtros — DAO pattern

`ClienteSpecification` (Criteria API) foi removido. O motivo: `cb.treat()` em herança JOINED gera JOINs implícitos nos dois subtipos simultaneamente quando usado em OR, podendo excluir registros ou retornar resultados incorretos. Além disso, `comFiltros` era chamado em um único ponto — sem reutilização que justificasse Specification.

**Implementação atual:**
- `ClienteRepositoryCustom` — interface com `buscarComFiltros(TipoPessoa, String, String, Pageable)`.
- `ClienteRepositoryCustomImpl` — usa `EntityManager` com JPQL. `TYPE(c)` guarda cada `TREAT`, garantindo INNER JOIN apenas no subtipo correto.
- `ClienteRepository` estende `JpaRepository` + `ClienteRepositoryCustom` (sem `JpaSpecificationExecutor`).

**Padrão JPQL usado em `ClienteRepositoryCustomImpl`:**
```jpql
FROM Cliente c
WHERE (:tipoPessoa IS NULL OR c.tipoPessoa = :tipoPessoa)
  AND (:doc IS NULL
       OR (TYPE(c) = ClientePf AND TREAT(c AS ClientePf).cpf        LIKE :docPattern)
       OR (TYPE(c) = ClientePj AND TREAT(c AS ClientePj).cnpj       LIKE :docPattern))
  AND (:nome IS NULL
       OR (TYPE(c) = ClientePf AND LOWER(TREAT(c AS ClientePf).nome)           LIKE :nomePattern)
       OR (TYPE(c) = ClientePj AND LOWER(TREAT(c AS ClientePj).razaoSocial)    LIKE :nomePattern))
```

A normalização de documento (remove não-dígitos) e nome (lowercase) ocorre em `ClienteRepositoryCustomImpl`, não no service — conhecimento de como os dados estão armazenados fica na camada de persistência.

### Unicidade de email
Sempre via `ClienteRepository.existsByEmail` (tabela pai `clientes`) — cobre PF e PJ na mesma query. **Não usar** `existsByEmail` em `ClientePfRepository` ou `ClientePjRepository`.

## DTOs

**Resposta:** sealed interface `ClienteDto` permits `ClientePfDto`, `ClientePjDto`. Ambos incluem `List<EnderecoDto> enderecos`.

**Entrada:** records com Bean Validation. `ClientePjUpdateDto` permite atualizar `razaoSocial` e `inscricaoEstadual`.

## Rotas

| Método | Caminho | Controller | Função |
|---|---|---|---|
| GET | `/clientes` | `ClienteController` | Listar com filtros (`tipoPessoa`, `documento`, `nome`) + paginação |
| GET | `/clientes/{id}` | `ClienteController` | Detalhe com endereços |
| DELETE | `/clientes/{id}` | `ClienteController` | Remover (cascata em endereços) |
| POST | `/clientes/pf` | `ClientePfController` | Criar PF |
| PATCH | `/clientes/pf/{id}` | `ClientePfController` | Atualizar PF |
| POST | `/clientes/pj` | `ClientePjController` | Criar PJ |
| PATCH | `/clientes/pj/{id}` | `ClientePjController` | Atualizar PJ |
