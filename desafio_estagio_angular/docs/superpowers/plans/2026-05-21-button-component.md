# ButtonComponent + Limpeza do AppComponent — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remover o template padrão do Angular CLI do AppComponent e criar o ButtonComponent com três variantes (primary, secondary, tertiary) dentro do SharedModule.

**Architecture:** O AppComponent vira uma casca mínima com apenas `<router-outlet>`. O ButtonComponent é um componente presentational (sem injeção de serviços) que usa tokens CSS do `styles.scss` para estilizar as três variantes via classes SCSS BEM, com suporte a ícone mat-icon opcional, estado loading (spinner) e estado disabled. O SharedModule declara e exporta o ButtonComponent, importando apenas `CommonModule`, `MatIconModule` e `MatProgressSpinnerModule`.

**Tech Stack:** Angular 14, Angular Material 14, SCSS, TypeScript 4.7

---

## Mapa de arquivos

| Arquivo | Operação | Responsabilidade |
| --- | --- | --- |
| `src/app/app.component.ts` | Modificar | Remover campo `title` |
| `src/app/app.component.html` | Modificar | Substituir por `<router-outlet>` |
| `src/app/app.component.scss` | Manter vazio | Sem alteração necessária |
| `src/app/shared/shared.module.ts` | Criar | Declara/exporta ButtonComponent + importa módulos Material |
| `src/app/shared/components/button/button.component.ts` | Criar | Lógica e API do ButtonComponent |
| `src/app/shared/components/button/button.component.html` | Criar | Template do botão |
| `src/app/shared/components/button/button.component.scss` | Criar | Estilos BEM das três variantes |
| `src/app/app.module.ts` | Modificar | Importar SharedModule |

---

## Task 1: Limpar o AppComponent

**Files:**
- Modify: `src/app/app.component.ts`
- Modify: `src/app/app.component.html`

- [ ] **Step 1: Remover o campo `title` do AppComponent**

Substituir o conteúdo completo de `src/app/app.component.ts` por:

```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {}
```

- [ ] **Step 2: Substituir o template padrão pelo router-outlet**

Substituir o conteúdo completo de `src/app/app.component.html` por:

```html
<router-outlet></router-outlet>
```

- [ ] **Step 3: Verificar que o projeto compila**

```bash
cd desafio_estagio_angular && npx ng build --configuration development 2>&1 | tail -20
```

Resultado esperado: `Build at: ...` sem erros de TypeScript. Warnings de orçamento são aceitáveis.

- [ ] **Step 4: Commit**

```bash
git add desafio_estagio_angular/src/app/app.component.ts desafio_estagio_angular/src/app/app.component.html
git commit -m "refactor: removendo template padrão do Angular CLI do AppComponent"
```

---

## Task 2: Criar o ButtonComponent

**Files:**
- Create: `src/app/shared/components/button/button.component.ts`
- Create: `src/app/shared/components/button/button.component.html`
- Create: `src/app/shared/components/button/button.component.scss`

- [ ] **Step 1: Criar o arquivo TypeScript do ButtonComponent**

Criar `src/app/shared/components/button/button.component.ts`:

```typescript
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss']
})
export class ButtonComponent {
  @Input() label = '';
  @Input() icon?: string;
  @Input() variant: 'primary' | 'secondary' | 'tertiary' = 'primary';
  @Input() loading = false;
  @Input() disabled = false;
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Output() clicked = new EventEmitter<void>();
}
```

- [ ] **Step 2: Criar o template HTML**

Criar `src/app/shared/components/button/button.component.html`:

```html
<button
  [class]="'erp-btn erp-btn--' + variant"
  [type]="type"
  [disabled]="loading || disabled"
  (click)="clicked.emit()">
  <mat-spinner *ngIf="loading" [diameter]="16"></mat-spinner>
  <mat-icon *ngIf="icon && !loading">{{ icon }}</mat-icon>
  {{ label }}
</button>
```

- [ ] **Step 3: Criar o SCSS com as três variantes**

Criar `src/app/shared/components/button/button.component.scss`:

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
  letter-spacing: 0.01em;
  cursor: pointer;
  border: 1px solid transparent;
  transition: opacity 0.15s, background-color 0.15s;
  white-space: nowrap;

  mat-icon {
    font-size: 1.1rem;
    width: 1.1rem;
    height: 1.1rem;
    line-height: 1.1rem;
  }

  mat-spinner {
    flex-shrink: 0;
  }

  &[disabled] {
    opacity: 0.5;
    cursor: default;
    pointer-events: none;
  }

  &--primary {
    background: var(--erp-success);
    color: #0d2318;
    border-color: transparent;

    &:hover:not([disabled]) {
      opacity: 0.88;
    }
  }

  &--secondary {
    background: var(--erp-surface);
    color: var(--erp-text-secondary);
    border-color: var(--erp-border-strong);

    &:hover:not([disabled]) {
      background: color-mix(in srgb, var(--erp-surface) 80%, white 20%);
    }
  }

  &--tertiary {
    background: transparent;
    color: var(--erp-text-secondary);
    border-color: var(--erp-success);

    mat-icon {
      color: var(--erp-success);
    }

    &:hover:not([disabled]) {
      background: var(--erp-success-glow);
    }
  }
}
```

> **Nota sobre `color-mix`:** é suportado em todos os navegadores modernos (Chrome 111+, Firefox 113+, Safari 16.2+). Se o projeto precisar suportar navegadores mais antigos, substituir por `rgba(255, 245, 230, 0.08)` (equivalente a `--erp-surface-muted`).

---

## Task 3: Criar o SharedModule e registrar o ButtonComponent

**Files:**
- Create: `src/app/shared/shared.module.ts`
- Modify: `src/app/app.module.ts`

- [ ] **Step 1: Criar o SharedModule**

Criar `src/app/shared/shared.module.ts`:

```typescript
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { ButtonComponent } from './components/button/button.component';

@NgModule({
  declarations: [ButtonComponent],
  imports: [
    CommonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  exports: [ButtonComponent],
})
export class SharedModule {}
```

- [ ] **Step 2: Importar o SharedModule no AppModule**

Editar `src/app/app.module.ts` para adicionar `SharedModule`:

```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SharedModule } from './shared/shared.module';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    SharedModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
```

- [ ] **Step 3: Verificar que o projeto compila sem erros**

```bash
cd desafio_estagio_angular && npx ng build --configuration development 2>&1 | tail -20
```

Resultado esperado: `Build at: ...` sem erros de TypeScript. Se houver erro de `Cannot find module '@angular/material/icon'`, verifique se `@angular/material` está instalado (`cat package.json | grep material`).

- [ ] **Step 4: Commit**

```bash
git add desafio_estagio_angular/src/app/shared/ desafio_estagio_angular/src/app/app.module.ts
git commit -m "feat: criando SharedModule e ButtonComponent com variantes primary, secondary e tertiary"
```

---

## Verificação final

Após os três tasks, o resultado deve ser:

- `app.component.html` contém apenas `<router-outlet></router-outlet>`
- `app.component.ts` não tem campo `title`
- `src/app/shared/shared.module.ts` existe e exporta `ButtonComponent`
- `src/app/shared/components/button/` contém os três arquivos (`.ts`, `.html`, `.scss`)
- `ng build --configuration development` passa sem erros TypeScript
