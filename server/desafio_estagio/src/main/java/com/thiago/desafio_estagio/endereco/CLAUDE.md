# Contexto: bounded context `endereco`

## Modelo

`Endereco` tem relação **unidirecional N:1** com `Cliente` — `Endereco.cliente` aponta via FK `cliente_id`. `Cliente` **não** mantém `List<Endereco>`.

Para listar endereços de um cliente: `EnderecoRepository.findByClienteId(UUID)`.

`@OnDelete(CASCADE)` na FK garante remoção em cascata via banco quando o cliente é deletado.

## Endereço principal

Apenas **um** endereço pode ser principal por cliente. `EnderecoService.criar` e `EnderecoService.atualizar` chamam `enderecoRepository.desmarcarTodosPrincipaisDoCliente(clienteId)` antes de gravar quando `enderecoPrincipal = true`.

## Campos imutáveis após criação

`PATCH /endereco/{id}` permite alterar apenas: `numero`, `telefone`, `complemento`, `enderecoPrincipal`.

Campos de localização são imutáveis: `cep`, `logradouro`, `bairro`, `cidade`, `estado`.

## Rotas

| Método | Caminho | Função |
|---|---|---|
| POST | `/endereco/{clienteId}` | Criar endereço |
| PATCH | `/endereco/{id}` | Atualizar (numero, telefone, complemento, enderecoPrincipal) |
| DELETE | `/endereco/{id}` | Deletar endereço |
