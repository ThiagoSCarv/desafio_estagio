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
|---|---|
| `app.component.html` | Substituir todo o conteúdo por `<router-outlet></router-outlet>` |
| `app.component.scss` | Esvaziar (estilos globais vivem em `styles.scss`) |
| `app.component.ts` | Remover campo `title`, manter apenas o decorator `@Component` |
| `app.module.ts` | Sem alteração |

---

## Trabalho 2 — ButtonComponent

### Localização

```
src/app/shared/
├── components/
│   └── button/
│       ├── button.component.ts
│       ├── button.component.html
│       └── button.component.scss
└── shared.module.ts
```

### Abordagem escolhida

Wrapper de `mat-raised-button` (Opção A). O tema Material já está configurado em `styles.scss` para usar `--erp-success` como cor primária — o botão herda cor, ripple, focus ring e `aria-disabled` automaticamente.

### API do componente

```typescript
@Input() label: string         // texto exibido no botão (obrigatório em uso)
@Input() icon?: string         // nome do mat-icon (opcional, renderizado à esquerda)
@Input() loading = false       // exibe mat-spinner (diameter=16) e desabilita
@Input() disabled = false      // desabilita sem spinner
@Input() type: 'button' | 'submit' | 'reset' = 'button'
@Output() clicked = new EventEmitter<void>()  // emite ao clicar
```

### Template

```html
<button
  mat-raised-button
  color="primary"
  [type]="type"
  [disabled]="loading || disabled"
  (click)="clicked.emit()">
  <mat-spinner *ngIf="loading" diameter="16" />
  <mat-icon *ngIf="icon && !loading">{{ icon }}</mat-icon>
  {{ label }}
</button>
```

### SCSS

Mínimo: `gap` entre ícone/spinner e texto, opacidade reduzida durante loading para sinalizar estado inativo.

```scss
button {
  gap: 0.5rem;
  display: inline-flex;
  align-items: center;

  &[disabled] {
    opacity: 0.6;
  }
}
```

### SharedModule

Declara e exporta `ButtonComponent`. Importa os módulos Material necessários:
- `MatButtonModule`
- `MatIconModule`
- `MatProgressSpinnerModule`
- `CommonModule` (para `*ngIf`)

---

## Fora do escopo

- Variantes Secondary, Ghost, Danger
- Tamanhos (sm / lg)
- Ícone à direita
- Testes unitários

---

## Critérios de sucesso

- `app.component.html` contém apenas `<router-outlet>`
- `ButtonComponent` compila sem erros
- `SharedModule` exporta `ButtonComponent`
- Ícone aparece à esquerda do texto quando `icon` é fornecido
- Spinner aparece e botão fica desabilitado quando `loading=true`
- Clicar no botão emite `clicked` (não emite quando `loading` ou `disabled`)
