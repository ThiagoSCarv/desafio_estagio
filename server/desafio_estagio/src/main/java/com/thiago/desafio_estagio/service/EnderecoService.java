package com.thiago.desafio_estagio.service;

import com.thiago.desafio_estagio.dto.EnderecoCreateDto;
import com.thiago.desafio_estagio.dto.EnderecoDto;
import com.thiago.desafio_estagio.exceptions.CepJaCadastradoException;
import com.thiago.desafio_estagio.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.models.Cliente;
import com.thiago.desafio_estagio.models.Endereco;
import com.thiago.desafio_estagio.repository.ClienteRepository;
import com.thiago.desafio_estagio.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;

    // Cadastra um novo endereco para o cliente informado.
    // Garante a invariante de que apenas um endereco pode ser principal: caso o novo seja principal,
    // todos os principais existentes sao desmarcados antes da gravacao.
    @Transactional
    public EnderecoDto criar(UUID clienteId, EnderecoCreateDto dto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(ClienteNaoEncontradoException::new);

        if (enderecoRepository.existsByClienteIdAndCep(clienteId, dto.cep())) {
            throw new CepJaCadastradoException();
        }

        boolean principal = Boolean.TRUE.equals(dto.enderecoPrincipal());
        if (principal) {
            enderecoRepository.desmarcarTodosPrincipaisDoCliente(clienteId);
        }

        Endereco endereco = new Endereco();
        endereco.setLogradouro(dto.logradouro());
        endereco.setNumero(dto.numero());
        endereco.setCep(dto.cep());
        endereco.setBairro(dto.bairro());
        endereco.setTelefone(dto.telefone());
        endereco.setCidade(dto.cidade());
        endereco.setEstado(dto.estado());
        endereco.setEnderecoPrincipal(principal);
        endereco.setComplemento(dto.complemento());
        endereco.setCliente(cliente);

        return EnderecoDto.from(enderecoRepository.save(endereco));
    }
}
