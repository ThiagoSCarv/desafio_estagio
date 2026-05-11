# Contexto: `shared/wicket`

Camada de frontend server-side usando **Apache Wicket** integrado ao Spring Boot.

## Stack

- Apache Wicket (integrado via `WicketConfig`)
- Bootstrap (via CDN ou WebJar — classes utilitárias de layout/componentes)
- JavaScript: máscaras de input (`IMask` ou `jQuery Mask Plugin`), cálculos client-side
- `FeedbackPanel` para mensagens de sucesso, erro, e alertas ao usuário
- `AjaxRequestTarget` para atualizações parciais de página sem reload

## Estrutura de pacotes

```
shared/wicket/
├── WicketConfig.java          # configuração da aplicação Wicket (montagem de páginas, filtro)
└── pages/
    └── home/                  # página inicial
        ├── HomePage.java
        └── HomePage.html
```

Cada página e painel seguem a convenção Wicket: **arquivo `.html` com mesmo nome e mesmo pacote** que a classe Java correspondente.

## Convenções de páginas e painéis

- Páginas herdam de `WebPage`.
- Painéis reutilizáveis herdam de `Panel`.
- Formulários usam `Form<T>` com `CompoundPropertyModel<T>`.
- Campos de input usam `TextField`, `DropDownChoice`, `DateTextField`, etc.
- Nunca usar `setResponsePage` dentro de handlers Ajax — preferir `AjaxRequestTarget.add(component)`.

## FeedbackPanel

Todo formulário deve ter um `FeedbackPanel` associado, com `setOutputMarkupId(true)` para permitir atualização via Ajax.

```java
FeedbackPanel feedback = new FeedbackPanel("feedback");
feedback.setOutputMarkupId(true);
add(feedback);
```

No HTML:
```html
<div wicket:id="feedback" class="mt-2"></div>
```

Mensagens são adicionadas via:
```java
// Sucesso
success("Cliente cadastrado com sucesso.");

// Erro
error("E-mail já cadastrado.");
```

O `FeedbackPanel` é sempre adicionado ao `AjaxRequestTarget` após qualquer operação para exibir a mensagem atualizada.

## AjaxRequestTarget

Usar `AjaxRequestTarget` para atualizar componentes sem recarregar a página inteira.

Padrão de uso em botões de submit Ajax:

```java
AjaxButton salvar = new AjaxButton("salvar") {
    @Override
    protected void onSubmit(AjaxRequestTarget target) {
        // lógica de negócio
        success("Salvo com sucesso.");
        target.add(feedback);
        target.add(outroComponente);
    }

    @Override
    protected void onError(AjaxRequestTarget target) {
        target.add(feedback);
    }
};
```

Sempre chamar `target.add(feedback)` tanto no `onSubmit` quanto no `onError`.

## Tema e design tokens

Arquivo de tema: `src/main/resources/static/css/theme.css`

Todo template base deve incluir:
```html
<!-- Bootstrap 5 -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3/dist/css/bootstrap.min.css"/>
<!-- Tema do projeto (sobrescreve variáveis Bootstrap + define tokens) -->
<link rel="stylesheet" href="/css/theme.css"/>
```

### Fontes
| Variável CSS | Família | Uso |
|---|---|---|
| `--erp-font-sans` | `Geist` (400, 500) | Toda a UI |
| `--erp-font-mono` | `Geist Mono` (400, 500) | Códigos, IDs, kbd |

### Paleta de cores
| Token | Valor | Uso |
|---|---|---|
| `--erp-bg` | `#14110f` | Fundo principal da página |
| `--erp-surface` | `#1c1815` | Cards, linhas da lista |
| `--erp-surface-muted` | `rgba(255,245,230,0.04)` | Inputs, badges mono |
| `--erp-text` | `#efe9e2` | Texto principal |
| `--erp-text-secondary` | `rgba(239,233,226,0.62)` | Labels, metadados |
| `--erp-text-muted` | `rgba(239,233,226,0.38)` | Placeholders, códigos |
| `--erp-accent` | `#d97757` | Botão primário, acento |
| `--erp-success` | `#6dbc8a` | Status ativo |
| `--erp-border` | `rgba(255,245,230,0.08)` | Bordas sutis |

Usar sempre os tokens CSS em vez de valores hexadecimais diretos nos templates.

## Bootstrap

Layout usa classes Bootstrap diretamente nos templates `.html`.

```html
<div class="container mt-4">
  <div class="row">
    <div class="col-md-6">
      <input wicket:id="nome" class="form-control" type="text" />
    </div>
  </div>
</div>
```

Botões de ação:
- Salvar: `btn btn-primary`
- Cancelar/Voltar: `btn btn-secondary`
- Excluir: `btn btn-danger`

Alertas do `FeedbackPanel` devem ser estilizados com classes Bootstrap (`alert alert-success`, `alert alert-danger`). Isso é feito sobrescrevendo `getCSSClass` no `FeedbackPanel` ou usando um `FeedbackPanel` customizado.

## JavaScript — Máscaras e Cálculos

Scripts são incluídos via `JavaScriptHeaderItem` ou diretamente no `<head>` do template base.

Máscaras de input são aplicadas via `behavior` ou via script no `onDomReady`:

```java
// Exemplo de behavior para máscara de CPF
component.add(new AttributeAppender("data-mask", "000.000.000-00"));
```

No HTML/JS (usando IMask ou jQuery Mask):
```javascript
document.querySelectorAll('[data-mask]').forEach(el => {
    IMask(el, { mask: el.dataset.mask });
});
```

Cálculos automáticos (ex: totais, idades) são feitos via JavaScript escutando eventos `input` ou `change` nos campos relevantes, sem depender de roundtrip ao servidor.

## Integração com a API REST

O frontend Wicket pode consumir os services Spring diretamente via injeção de dependência (Spring gerencia os beans Wicket quando configurado com `@SpringBean`).

```java
@SpringBean
private ClienteService clienteService;
```

Nunca injetar repositórios diretamente nas páginas — sempre usar services.

## Mensagens de feedback por operação

| Operação | Mensagem padrão |
|---|---|
| Cadastro com sucesso | `"Cliente cadastrado com sucesso."` |
| Alteração com sucesso | `"Cliente atualizado com sucesso."` |
| Exclusão com sucesso | `"Cliente removido com sucesso."` |
| Erro de validação | mensagem do validator (Bean Validation ou Wicket) |
| Erro de negócio (ex: CPF duplicado) | mensagem da exceção de domínio |

Erros de domínio capturados no `onSubmit` devem chamar `error(e.getMessage())` e `target.add(feedback)`.
