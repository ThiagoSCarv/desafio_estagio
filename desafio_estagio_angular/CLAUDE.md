# CLAUDE.md — Guia de Arquitetura do Projeto

> Documento de referência para decisões de arquitetura, estrutura de pastas e convenções adotadas neste projeto Angular 14 + Angular Material.

---

## Stack

- **Framework:** Angular 14
- **UI Library:** Angular Material
- **Linguagem:** TypeScript
- **Estilos:** SCSS

---

## Estrutura de Pastas

```
src/
└── app/
    │
    ├── core/                                   # Serviços singleton, guards, interceptors
    │   ├── services/
    │   │   └── customer.service.ts             # Chamadas HTTP para a API de clientes
    │   └── models/
    │       └── customer.model.ts               # Interface do objeto Cliente
    │
    ├── shared/                                 # Componentes agnósticos de domínio
    │   ├── components/
    │   │   ├── button/
    │   │   │   ├── button.component.ts
    │   │   │   ├── button.component.html
    │   │   │   └── button.component.scss
    │   │   ├── input/
    │   │   │   ├── input.component.ts
    │   │   │   ├── input.component.html
    │   │   │   └── input.component.scss
    │   │   └── icon-button/
    │   │       ├── icon-button.component.ts
    │   │       ├── icon-button.component.html
    │   │       └── icon-button.component.scss
    │   └── shared.module.ts                    # Importa e exporta módulos do Material + componentes acima
    │
    ├── features/
    │   │
    │   ├── customer-list/                      # Página de listagem de clientes
    │   │   ├── components/                     # Componentes inteligentes desta feature
    │   │   │   ├── customer-list/
    │   │   │   │   ├── customer-list.component.ts
    │   │   │   │   ├── customer-list.component.html
    │   │   │   │   └── customer-list.component.scss
    │   │   │   └── search-filters/
    │   │   │       ├── search-filters.component.ts
    │   │   │       ├── search-filters.component.html
    │   │   │       └── search-filters.component.scss
    │   │   ├── customer-list-page.component.ts  # Componente "página" (smart container)
    │   │   ├── customer-list-page.component.html
    │   │   ├── customer-list-page.component.scss
    │   │   └── customer-list.module.ts
    │   │
    │   └── customer-detail/                    # Página de detalhes de um cliente
    │       ├── customer-detail-page.component.ts
    │       ├── customer-detail-page.component.html
    │       ├── customer-detail-page.component.scss
    │       └── customer-detail.module.ts
    │
    ├── app-routing.module.ts
    ├── app.component.ts
    ├── app.component.html
    ├── app.component.scss
    └── app.module.ts
```

---

## Responsabilidade de cada camada

### `core/`
Tudo que é instanciado **uma única vez** na aplicação. Nunca deve ser importado em feature modules — apenas no `AppModule`.

| Arquivo | Responsabilidade |
|---|---|
| `customer.service.ts` | Métodos `getAll()` e `getById(id)` para consumo da API |
| `customer.model.ts` | Interface TypeScript que define o contrato do objeto `Customer` |

### `shared/`
Componentes **burros (dumb/presentational)**: não injetam serviços, não conhecem o domínio, comunicam-se apenas via `@Input()` e `@Output()`.

> **Regra prática:** se você consegue copiar o componente para outro projeto Angular completamente diferente e ele ainda faz sentido — ele pertence ao `shared/`. Se não, ele pertence à feature.

### `features/`
Cada subpasta representa uma rota da aplicação. Componentes que vivem dentro de `features/` podem ser:

- **Page component (smart):** orquestra a lógica, injeta serviços, gerencia estado.
- **Feature components:** conhecem o domínio local (ex: `SearchFilters` entende o que é um filtro de cliente), mas só são usados dentro dessa feature.

---

## Roteamento

As rotas utilizam **lazy loading** para carregar cada feature module sob demanda.

```typescript
// app-routing.module.ts
const routes: Routes = [
  {
    path: '',
    redirectTo: 'customers',
    pathMatch: 'full'
  },
  {
    path: 'customers',
    loadChildren: () =>
      import('./features/customer-list/customer-list.module')
        .then(m => m.CustomerListModule)
  },
  {
    path: 'customers/:id',
    loadChildren: () =>
      import('./features/customer-detail/customer-detail.module')
        .then(m => m.CustomerDetailModule)
  }
];
```

---

## Convenções

- **Interfaces > Classes** para modelos de dados vindos de API.
- **SCSS** por componente, sem estilos globais exceto em `styles.scss`.
- **Sufixos obrigatórios** nos nomes de arquivo: `.component`, `.module`, `.service`, `.model`.
- Componentes de página recebem o sufixo `-page` para diferenciar de componentes internos da feature.
- Módulos do Angular Material são importados e re-exportados **apenas** pelo `SharedModule`.
