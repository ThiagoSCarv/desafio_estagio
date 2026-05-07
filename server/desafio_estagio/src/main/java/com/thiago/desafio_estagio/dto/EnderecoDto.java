package com.thiago.desafio_estagio.dto;

import com.thiago.desafio_estagio.models.Endereco;

import java.util.UUID;

// Resposta de Endereco. Nao expoe a entidade nem a referencia ao Cliente.
public record EnderecoDto(
        UUID id,
        String logradouro,
        String numero,
        String cep,
        String bairro,
        String telefone,
        String cidade,
        String estado,
        boolean enderecoPrincipal,
        String complemento
) {

    public static EnderecoDto from(Endereco endereco) {
        return new EnderecoDto(
                endereco.getId(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getCep(),
                endereco.getBairro(),
                endereco.getTelefone(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.isEnderecoPrincipal(),
                endereco.getComplemento()
        );
    }
}
