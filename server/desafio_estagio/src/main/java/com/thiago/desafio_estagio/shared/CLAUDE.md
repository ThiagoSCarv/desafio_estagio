# Contexto: `shared`

## Tratamento de erros

`ExceptionHandlerController` (anotado com `@RestControllerAdvice`) centraliza o tratamento de todas as exceções de domínio. Controllers **nunca** usam `try/catch` — erros propagam e o handler devolve o status correto (404, 409, 422).

`ErrorMessageDTO` é o DTO de resposta de erro padrão.

## Validações customizadas

| Anotação | Valida |
|---|---|
| `@ValidCpf` | CPF (11 dígitos + dígitos verificadores) |
| `@ValidCnpj` | CNPJ (14 dígitos + dígitos verificadores) |
| `@ValidRg` | RG alfanumérico |
| `@ValidCep` | CEP (8 dígitos) |
| `@ValidTelefone` | Telefone (10 ou 11 dígitos) |

As anotações ficam em `validation/annotation/`, os validators em `validation/validator/`.
