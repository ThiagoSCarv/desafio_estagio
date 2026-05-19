package com.thiago.desafio_estagio.cliente.application;

import com.thiago.desafio_estagio.cliente.domain.Cliente;
import com.thiago.desafio_estagio.cliente.domain.ClientePf;
import com.thiago.desafio_estagio.cliente.domain.ClientePj;
import com.thiago.desafio_estagio.cliente.domain.ClienteRepository;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
import com.thiago.desafio_estagio.endereco.domain.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public long contarTotal() {
        return clienteRepository.count();
    }

    @Transactional(readOnly = true)
    public long contarAtivos() {
        return clienteRepository.countByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Page<ClienteDto> listarTodos(TipoPessoa tipoPessoa, String documento, String nome, Pageable pageable) {
        return clienteRepository.buscarComFiltros(tipoPessoa, documento, nome, pageable).map(cliente -> toDto(cliente, List.of()));
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
        return switch (cliente) {
            case ClientePf pf -> ClientePfDto.from(pf, enderecos);
            case ClientePj pj -> ClientePjDto.from(pj, enderecos);
        };
    }

}
