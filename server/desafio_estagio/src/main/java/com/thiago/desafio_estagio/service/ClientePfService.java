package com.thiago.desafio_estagio.service;

import com.thiago.desafio_estagio.dto.ClientePfCreateDto;
import com.thiago.desafio_estagio.dto.EnderecoCreateDto;
import com.thiago.desafio_estagio.models.ClientePf;
import com.thiago.desafio_estagio.models.Endereco;
import com.thiago.desafio_estagio.models.TipoPessoa;
import com.thiago.desafio_estagio.exceptions.CpfJaCadastradoException;
import com.thiago.desafio_estagio.exceptions.EmailJaCadastradoException;
import com.thiago.desafio_estagio.exceptions.RgJaCadastradoException;
import com.thiago.desafio_estagio.repository.ClientePfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientePfService {

    @Autowired
    private ClientePfRepository clientePfRepository;

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
