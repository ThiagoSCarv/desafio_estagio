package com.thiago.desafio_estagio.service;

import com.thiago.desafio_estagio.dto.ClienteDto;
import com.thiago.desafio_estagio.dto.ClientePfDto;
import com.thiago.desafio_estagio.dto.ClientePjDto;
import com.thiago.desafio_estagio.dto.EnderecoDto;
import com.thiago.desafio_estagio.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.models.Cliente;
import com.thiago.desafio_estagio.models.ClientePf;
import com.thiago.desafio_estagio.models.ClientePj;
import com.thiago.desafio_estagio.models.TipoPessoa;
import com.thiago.desafio_estagio.repository.ClienteRepository;
import com.thiago.desafio_estagio.repository.ClienteSpecification;
import com.thiago.desafio_estagio.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;

    @Transactional(readOnly = true)
    public Page<ClienteDto> listarTodos(TipoPessoa tipoPessoa, String documento, String nome, Pageable pageable) {
        Specification<Cliente> spec = ClienteSpecification.comFiltros(tipoPessoa, documento, nome);
        return clienteRepository.findAll(spec, pageable).map(c -> toDto(c, List.of()));
    }

    @Transactional(readOnly = true)
    public ClienteDto buscarPorId(UUID id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(ClienteNaoEncontradoException::new);

        List<EnderecoDto> enderecos = enderecoRepository.findByClienteId(id)
                .stream().map(EnderecoDto::from).toList();

        return toDto(cliente, enderecos);
    }

    @Transactional
    public void deletar(UUID id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(ClienteNaoEncontradoException::new);

        clienteRepository.delete(cliente);
    }

    private ClienteDto toDto(Cliente cliente, List<EnderecoDto> enderecos) {
        if (cliente instanceof ClientePf pf) {
            return ClientePfDto.from(pf, enderecos);
        }
        return ClientePjDto.from((ClientePj) cliente, enderecos);
    }

}
