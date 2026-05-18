package com.thiago.desafio_estagio.endereco.application;

import com.thiago.desafio_estagio.endereco.domain.exceptions.CepNaoEncontradoException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class ViaCepClient {

    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    private final RestClient restClient = RestClient.create();

    // Lança CepNaoEncontradoException quando o CEP não existe (API retorna {"erro":"true"})
    // ou quando o formato é inválido (API retorna HTTP 400).
    public ViaCepResponseDto buscarPorCep(String cep) {
        String cepSomenteDigitos = cep.replaceAll("\\D", "");
        ViaCepResponseDto response;
        try {
            response = restClient.get()
                    .uri(VIA_CEP_URL, cepSomenteDigitos)
                    .retrieve()
                    .body(ViaCepResponseDto.class);
        } catch (RestClientResponseException e) {
            throw new CepNaoEncontradoException();
        }

        if (response == null || response.erro() != null) {
            throw new CepNaoEncontradoException();
        }

        return response;
    }
}
