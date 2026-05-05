package com.thiago.desafio_estagio.service;

import com.thiago.desafio_estagio.dto.EnderecoCreateDto;
import com.thiago.desafio_estagio.exceptions.CepJaCadastradoException;
import com.thiago.desafio_estagio.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.models.Cliente;
import com.thiago.desafio_estagio.models.Endereco;
import com.thiago.desafio_estagio.repository.ClienteRepository;
import com.thiago.desafio_estagio.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    public Endereco criar(UUID clienteId, EnderecoCreateDto dto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(ClienteNaoEncontradoException::new);

        if (enderecoRepository.existsByClienteIdAndCep(clienteId, dto.getCep())) {
            throw new CepJaCadastradoException();
        }

        Endereco endereco = new Endereco();
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setCep(dto.getCep());
        endereco.setBairro(dto.getBairro());
        endereco.setTelefone(dto.getTelefone());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setEnderecoPrincipal(dto.getEnderecoPrincipal());
        endereco.setComplemento(dto.getComplemento());
        endereco.setCliente(cliente);

        return enderecoRepository.save(endereco);
    }

    @Transactional
    public Cliente adicionarEndereco(UUID clienteId, Endereco novoEndereco) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(ClienteNaoEncontradoException::new);

        novoEndereco.setCliente(cliente);

        List<Endereco> enderecos = cliente.getEnderecos();

        if (novoEndereco.isEnderecoPrincipal()) {
            if (!enderecos.isEmpty()) {
                enderecos.get(0).setEnderecoPrincipal(false);
            }
            enderecos.add(0, novoEndereco);
        } else {
            enderecos.add(novoEndereco);
        }

        return clienteRepository.save(cliente);
    }
}
