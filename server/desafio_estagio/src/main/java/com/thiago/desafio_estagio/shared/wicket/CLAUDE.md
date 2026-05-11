# Contexto: `shared/wicket`

Camada de frontend server-side usando **Apache Wicket** integrado ao Spring Boot.

## Stack

- Apache Wicket 10.x (`wicket-spring:10.2.0`) + Objenesis 3.4
- Bootstrap 5.3 (via CDN — injetado pelo `renderHead` de cada página)
- JavaScript: máscaras de input (`IMask` ou `jQuery Mask Plugin`), cálculos client-side
- `FeedbackPanel` para mensagens de sucesso, erro e alertas ao usuário
- `AjaxRequestTarget` para atualizações parciais de página sem reload

## Dependências no pom.xml

```xml
<dependency>
    <groupId>org.apache.wicket</groupId>
    <artifactId>wicket-spring</artifactId>
    <version>10.2.0</version>
</dependency>
<dependency>
    <groupId>org.objenesis</groupId>
    <artifactId>objenesis</artifactId>
    <version>3.4</version>
</dependency>
```

> **Objenesis é obrigatório.** Sem ele o Wicket não consegue criar proxies para beans Spring que não têm construtor padrão (ex: services com `@RequiredArgsConstructor`), lançando `Can't create proxy... without default constructor`.

O `pom.xml` também precisa expor arquivos não-Java do `src/main/java` no classpath:

```xml
<build>
    <resources>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.html</include>
                <include>**/*.properties</include>
                <include>**/*.css</include>
            </includes>
        </resource>
        <resource>
            <directory>src/main/resources</directory>
        </resource>
    </resources>
</build>
```

## Estrutura de pacotes

```
shared/wicket/
├── WicketApplication.java     # WebApplication @Component — registra SpringComponentInjector e monta recursos
├── WicketConfig.java          # @Configuration — registra WicketFilter via FilterRegistrationBean
├── theme.css                  # CSS do tema (PackageResource servido pelo Wicket)
└── pages/
    └── home/
        ├── HomePage.java
        └── HomePage.html
```

Cada página e painel seguem a convenção Wicket: **arquivo `.html` com mesmo nome e mesmo pacote** que a classe Java correspondente.

## Integração Spring Boot

### WicketApplication

```java
@Component
public class WicketApplication extends WebApplication {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    @Override
    protected void init() {
        super.init();
        // Habilita @SpringBean nas páginas e painéis
        getComponentInstantiationListeners().add(
            new SpringComponentInjector(this, applicationContext)
        );
        // Monta recursos CSS como PackageResourceReference para o Wicket servir diretamente
        mountResource("/css/theme.css",
            new PackageResourceReference(WicketApplication.class, "theme.css")
        );
    }
}
```

### WicketConfig

```java
@Configuration
public class WicketConfig {

    @Bean
    public FilterRegistrationBean<WicketFilter> wicketFilter() {
        FilterRegistrationBean<WicketFilter> registration = new FilterRegistrationBean<>();
        WicketFilter filter = new WicketFilter();
        registration.setFilter(filter);
        registration.addInitParameter(
            WicketFilter.APP_FACT_PARAM,
            "org.apache.wicket.spring.SpringWebApplicationFactory"
        );
        registration.addInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }
}
```

O `WicketFilter` em `/*` intercepta todos os requests. URLs que não correspondem a nenhuma página/recurso Wicket chamam `chain.doFilter()` automaticamente, permitindo que os endpoints REST (`/clientes`, `/endereco`, etc.) continuem funcionando normalmente via `DispatcherServlet`.

## Convenções de páginas e painéis

- Páginas herdam de `WebPage`.
- Painéis reutilizáveis herdam de `Panel`.
- Formulários usam `Form<T>` com `CompoundPropertyModel<T>`.
- Campos de input usam `TextField`, `DropDownChoice`, `DateTextField`, etc.
- Nunca usar `setResponsePage` dentro de handlers Ajax — preferir `AjaxRequestTarget.add(component)`.

## CSS e JavaScript — como incluir

**Nunca colocar `<link>` ou `<script>` diretamente no HTML.** Usar `renderHead()` na classe da página ou painel:

```java
@Override
public void renderHead(IHeaderResponse response) {
    super.renderHead(response);
    // Bootstrap via CDN
    response.render(CssUrlReferenceHeaderItem.forUrl(
        "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
    ));
    // Tema do projeto (servido pelo Wicket como PackageResource)
    response.render(CssHeaderItem.forReference(
        new PackageResourceReference(WicketApplication.class, "theme.css")
    ));
    // Bootstrap JS via CDN
    response.render(JavaScriptUrlReferenceHeaderItem.forUrl(
        "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
    ));
}
```

O HTML deve ter `<wicket:head/>` dentro do `<head>` para Wicket injetar os itens:

```html
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Título da Página</title>
    <wicket:head/>
</head>
```

> **Por que PackageResource e não `/static`?** O `WicketFilter` está mapeado em `/*` com prioridade 1. Como filtros sempre executam antes de servlets, requests para `/css/theme.css` chegam ao Wicket antes do `ResourceHttpRequestHandler` do Spring Boot. Ao montar o recurso no Wicket, ele serve o arquivo diretamente sem depender do handler estático.

## Tema e design tokens

O arquivo `theme.css` fica em `src/main/java/.../shared/wicket/theme.css` (mesmo pacote que `WicketApplication`).

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
| `--erp-success` | `#6dbc8a` | Status ativo, título itálico |
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

## FeedbackPanel

Todo formulário deve ter um `FeedbackPanel` com `setOutputMarkupId(true)`.

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
success("Cliente cadastrado com sucesso.");
error("E-mail já cadastrado.");
```

O `FeedbackPanel` é sempre adicionado ao `AjaxRequestTarget` após qualquer operação.

## AjaxRequestTarget

Usar `AjaxRequestTarget` para atualizar componentes sem recarregar a página.

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

## JavaScript — Máscaras e Cálculos

Scripts são incluídos via `renderHead` com `JavaScriptUrlReferenceHeaderItem` ou `JavaScriptHeaderItem`.

Máscaras de input são aplicadas via `behavior` com atributo `data-mask`:

```java
component.add(new AttributeAppender("data-mask", "000.000.000-00"));
```

No JS (usando IMask):
```javascript
document.querySelectorAll('[data-mask]').forEach(el => {
    IMask(el, { mask: el.dataset.mask });
});
```

Cálculos automáticos (totais, idades) são feitos via JS escutando eventos `input`/`change`, sem roundtrip ao servidor.

## Integração com Services Spring

Injetar services via `@SpringBean` (nunca repositórios diretamente):

```java
@SpringBean
private ClienteService clienteService;
```

## Mensagens de feedback por operação

| Operação | Mensagem padrão |
|---|---|
| Cadastro com sucesso | `"Cliente cadastrado com sucesso."` |
| Alteração com sucesso | `"Cliente atualizado com sucesso."` |
| Exclusão com sucesso | `"Cliente removido com sucesso."` |
| Erro de validação | mensagem do validator (Bean Validation ou Wicket) |
| Erro de negócio (ex: CPF duplicado) | mensagem da exceção de domínio |

Erros de domínio capturados no `onSubmit` devem chamar `error(e.getMessage())` e `target.add(feedback)`.
