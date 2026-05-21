# Design: ButtonComponent + Limpeza do AppComponent

**Data:** 2026-05-21
**Escopo:** Angular 16 — `desafio_estagio_angular`

---

## Contexto

O projeto Angular foi inicializado com o template padrão do Angular CLI. O `app.component.html` contém a welcome page padrão (toolbar, cards de recursos, footer). O `styles.scss` já tem um sistema de design completo (tokens ERP, tema Material dark). Nenhuma estrutura de pastas (`shared/`, `core/`, `features/`) existe ainda.

Esta spec cobre dois trabalhos independentes executados juntos:

1. Remover o boilerplate padrão do Angular CLI
2. Criar o primeiro componente do `SharedModule`: `ButtonComponent`

---

## Trabalho 1 — Limpeza do AppComponent

### Objetivo

Deixar o `AppComponent` como casca mínima, delegando todo o visual para o roteador.

### Mudanças

| Arquivo | Ação |
| --- | --- |
| `app.component.html` | Substituir todo o conteúdo por `<router-outlet></router-outlet>` |
| `app.component.scss` | Esvaziar (estilos globais vivem em `styles.scss`) |
| `app.component.ts` | Remover campo `title`, manter apenas o decorator `@Component` |
| `app.module.ts` | Sem alteração |

---

## Trabalho 2 — ButtonComponent

### Localização

```text
src/app/shared/
├── components/
│   └── button/
│       ├── button.component.ts
│       ├── button.component.html
│       └── button.component.scss
└── shared.module.ts
```

### Abordagem

Wrapper sobre `mat-raised-button` (Primary) e botões nativos estilizados com tokens CSS para Secondary e Tertiary. O componente usa `:host` + classes derivadas do `variant` para aplicar os estilos corretos, mantendo o mesmo elemento `<button>` base.

### Variantes (referência visual)

| Variante | Aparência | Mapeamento de tokens |
| --- | --- | --- |
| `primary` | Fundo verde sólido, texto escuro | `bg: --erp-success`, `color: #0d2318` |
| `secondary` | Fundo `--erp-surface`, borda `--erp-border-strong`, texto `--erp-text-secondary` | sem `mat-raised`, borda visível |
| `tertiary` | Fundo transparente/surface, borda e ícone em `--erp-success` | destaque apenas no ícone e borda |

### API do componente

```typescript
@Input() label: string                               // texto do botão (obrigatório em uso)
@Input() icon?: string                               // nome do mat-icon (opcional, à esquerda)
@Input() variant: 'primary' | 'secondary' | 'tertiary' = 'primary'
@Input() loading = false                             // spinner (diameter=16) + desabilita
@Input() disabled = false                            // desabilita sem spinner
@Input() type: 'button' | 'submit' | 'reset' = 'button'
@Output() clicked = new EventEmitter<void>()
```

### Template

```html
<button
  [class]="'erp-btn erp-btn--' + variant"
  [type]="type"
  [disabled]="loading || disabled"
  (click)="clicked.emit()">
  <mat-spinner *ngIf="loading" diameter="16" />
  <mat-icon *ngIf="icon && !loading">{{ icon }}</mat-icon>
  {{ label }}
</button>
```

> Não usa `mat-raised-button` diretamente — o estilo primary é replicado com tokens CSS para manter consistência entre as três variantes sem misturar APIs do Material.

### SCSS

```scss
:host {
  display: inline-flex;
}

.erp-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0 1rem;
  height: 2.25rem;
  border-radius: 0.375rem;
  font-family: var(--erp-font-sans);
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  border: 1px solid transparent;
  transition: opacity 0.15s, background-color 0.15s;

  &[disabled] { opacity: 0.5; cursor: default; }

  &--primary {
    background: var(--erp-success);
    color: #0d2318;
    border-color: transparent;
    &:hover:not([disabled]) { opacity: 0.88; }
  }

  &--secondary {
    background: var(--erp-surface);
    color: var(--erp-text-secondary);
    border-color: var(--erp-border-strong);
    &:hover:not([disabled]) { background: var(--erp-surface-muted); }
  }

  &--tertiary {
    background: transparent;
    color: var(--erp-text-secondary);
    border-color: var(--erp-success);
    mat-icon { color: var(--erp-success); }
    &:hover:not([disabled]) { background: var(--erp-success-glow); }
  }
}
```

### SharedModule

Declara e exporta `ButtonComponent`. Importa:

- `CommonModule` (para `*ngIf`)
- `MatIconModule`
- `MatProgressSpinnerModule`

> `MatButtonModule` não é necessário — o botão é nativo com CSS próprio.

---

## Fora do escopo

- Tamanhos (sm / lg)
- Ícone à direita
- Testes unitários

---

## Critérios de sucesso

- `app.component.html` contém apenas `<router-outlet>`
- `ButtonComponent` compila sem erros TypeScript
- `SharedModule` declara e exporta `ButtonComponent`
- Variante `primary` exibe fundo verde sólido
- Variante `secondary` exibe fundo escuro com borda sutil
- Variante `tertiary` exibe borda e ícone em verde (`--erp-success`)
- Ícone aparece à esquerda do texto quando `icon` é fornecido
- Spinner substitui o ícone e desabilita o botão quando `loading=true`
- Botão não emite `clicked` quando `loading` ou `disabled`
