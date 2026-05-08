package com.thiago.desafio_estagio.cliente.application;

import com.thiago.desafio_estagio.cliente.domain.ClientePf;
import com.thiago.desafio_estagio.cliente.domain.ClientePfRepository;
import com.thiago.desafio_estagio.cliente.domain.ClienteRepository;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.CpfJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.EmailJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.RgJaCadastradoException;
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
