package com.thiago.desafio_estagio.service;

import com.thiago.desafio_estagio.dto.ClientePfCreateDto;
import com.thiago.desafio_estagio.dto.ClientePfDto;
import com.thiago.desafio_estagio.dto.ClientePfUpdateDto;
import com.thiago.desafio_estagio.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.models.ClientePf;
import com.thiago.desafio_estagio.models.TipoPessoa;
import com.thiago.desafio_estagio.exceptions.CpfJaCadastradoException;
import com.thiago.desafio_estagio.exceptions.EmailJaCadastradoException;
import com.thiago.desafio_estagio.exceptions.RgJaCadastradoException;
import com.thiago.desafio_estagio.repository.ClientePfRepository;
import com.thiago.desafio_estagio.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientePfService {

    private final ClientePfRepository clientePfRepository;
    private final ClienteRepository clienteRepository;

    //Cria um novo Cliente PF validando que não existe outro email igual já cadastrado na tabela Cliente
    //CPF e RG são normalizados garantindo que mesmo que os clientes enviem com mascara o dado seja armazenado da maneira correta.
    @Transactional
    public ClientePfDto criar(ClientePfCreateDto dto) {
        String cpf = dto.cpf().replaceAll("[^0-9]", "");
        String rg = dto.rg().replaceAll("[.\\-\\s]", "").toUpperCase();

        if (clienteRepository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException();
        }
        if (clientePfRepository.existsByCpf(cpf)) {
            throw new CpfJaCadastradoException();
        }
        if (clientePfRepository.existsByRg(rg)) {
            throw new RgJaCadastradoException();
        }

        ClientePf clientePf = new ClientePf();
        clientePf.setTipoPessoa(TipoPessoa.FISICA);
        clientePf.setEmail(dto.email());
        clientePf.setNome(dto.nome());
        clientePf.setCpf(cpf);
        clientePf.setRg(rg);
        clientePf.setDataNascimento(dto.dataNascimento());

        return ClientePfDto.from(clientePfRepository.save(clientePf), List.of());
    }

    @Transactional
    public ClientePfDto atualizar(UUID id, ClientePfUpdateDto dto) {
        ClientePf clientePf = clientePfRepository.findById(id)
                .orElseThrow(ClienteNaoEncontradoException::new);

        if (dto.email() != null && !dto.email().equals(clientePf.getEmail())) {
            if (clienteRepository.existsByEmailAndIdNot(dto.email(), id)) {
                throw new EmailJaCadastradoException();
            }
            clientePf.setEmail(dto.email());
        }

        if (dto.nome() != null) {
            clientePf.setNome(dto.nome());
        }

        if (dto.ativo() != null) {
            clientePf.setAtivo(dto.ativo());
        }

        return ClientePfDto.from(clientePfRepository.save(clientePf), List.of());
    }
}
