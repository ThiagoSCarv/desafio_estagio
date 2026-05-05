package com.thiago.desafio_estagio.service;

import com.thiago.desafio_estagio.dto.ClientePfCreateDto;
import com.thiago.desafio_estagio.dto.ClientePfUpdateDto;
import com.thiago.desafio_estagio.dto.EnderecoCreateDto;
import com.thiago.desafio_estagio.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.models.ClientePf;
import com.thiago.desafio_estagio.models.Endereco;
import com.thiago.desafio_estagio.models.TipoPessoa;
import com.thiago.desafio_estagio.exceptions.CpfJaCadastradoException;
import com.thiago.desafio_estagio.exceptions.EmailJaCadastradoException;
import com.thiago.desafio_estagio.exceptions.RgJaCadastradoException;
import com.thiago.desafio_estagio.repository.ClientePfRepository;
import com.thiago.desafio_estagio.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClientePfService {

    @Autowired
    private ClientePfRepository clientePfRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    public ClientePf criar(ClientePfCreateDto dto) {
        if (clientePfRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaCadastradoException();
        }
        if (clientePfRepository.existsByCpf(dto.getCpf())) {
            throw new CpfJaCadastradoException();
        }
        if (clientePfRepository.existsByRg(dto.getRg())) {
            throw new RgJaCadastradoException();
        }

        ClientePf clientePf = new ClientePf();
        clientePf.setTipoPessoa(TipoPessoa.FISICA);
        clientePf.setEmail(dto.getEmail());
        clientePf.setNome(dto.getNome());
        clientePf.setCpf(dto.getCpf());
        clientePf.setRg(dto.getRg());
        clientePf.setDataNascimento(dto.getDataNascimento());

        if (dto.getEnderecos() != null) {
            List<Endereco> enderecos = dto.getEnderecos().stream()
                    .map(enderecoDto -> mapearEndereco(enderecoDto, clientePf))
                    .toList();
            clientePf.getEnderecos().addAll(enderecos);
        }

        return clientePfRepository.save(clientePf);
    }

    @Transactional
    public ClientePf atualizar(UUID id, ClientePfUpdateDto dto) {
        ClientePf clientePf = clientePfRepository.findById(id)
                .orElseThrow(ClienteNaoEncontradoException::new);

        if (dto.getEmail() != null && !dto.getEmail().equals(clientePf.getEmail())) {
            if (clienteRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
                throw new EmailJaCadastradoException();
            }
            clientePf.setEmail(dto.getEmail());
        }

        if (dto.getNome() != null) {
            clientePf.setNome(dto.getNome());
        }

        if (dto.getAtivo() != null) {
            clientePf.setAtivo(dto.getAtivo());
        }

        return clientePfRepository.save(clientePf);
    }

    private Endereco mapearEndereco(EnderecoCreateDto dto, ClientePf clientePf) {
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
        endereco.setCliente(clientePf);
        return endereco;
    }
}
