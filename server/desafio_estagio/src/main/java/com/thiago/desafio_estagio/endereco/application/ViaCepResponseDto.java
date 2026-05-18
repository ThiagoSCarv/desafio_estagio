package com.thiago.desafio_estagio.endereco.application;

// Mapeamento da resposta da API ViaCEP. Quando o CEP não é encontrado,
// a API retorna {"erro": "true"} — nesse caso os demais campos ficam nulos.
public record ViaCepResponseDto(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf,
        String erro
) {
}
