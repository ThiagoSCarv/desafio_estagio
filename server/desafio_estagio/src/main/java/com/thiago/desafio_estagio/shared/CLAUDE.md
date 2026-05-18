# Contexto: `shared`

## Tratamento de erros

`ExceptionHandlerController` (anotado com `@RestControllerAdvice`) centraliza o tratamento de todas as exceções de domínio. Controllers **nunca** usam `try/catch` — erros propagam e o handler devolve o status correto (404, 409, 422).

`ErrorMessageDTO` é o DTO de resposta de erro padrão.

## Validações customizadas

| Anotação | Valida | Algoritmo |
|---|---|---|
| `@ValidCpf` | CPF (11 dígitos + dígitos verificadores) | Caelum Stella `CPFValidator` |
| `@ValidCnpj` | CNPJ (14 dígitos + dígitos verificadores) | Caelum Stella `CNPJValidator` |
| `@ValidRg` | RG alfanumérico | implementação local |
| `@ValidCep` | CEP (8 dígitos) | implementação local |
| `@ValidTelefone` | Telefone (10 ou 11 dígitos) | implementação local |

As anotações ficam em `validation/annotation/`, os validators em `validation/validator/`.

**Caelum Stella (CPF/CNPJ):** o módulo oficial `caelum-stella-bean-validation` ainda usa `javax.validation` e não interopera com Jakarta Validation (Spring Boot 3). Por isso só importamos `caelum-stella-core` (algoritmos puros) e mantemos nossas anotações Jakarta `@ValidCpf` / `@ValidCnpj`, cujos `ConstraintValidator`s delegam para `br.com.caelum.stella.validation.CPFValidator` / `CNPJValidator`. O valor é normalizado (`replaceAll("\\D", "")`) antes da delegação para aceitar entrada com ou sem máscara.
