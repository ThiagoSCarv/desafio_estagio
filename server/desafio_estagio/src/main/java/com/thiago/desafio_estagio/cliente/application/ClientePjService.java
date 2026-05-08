package com.thiago.desafio_estagio.service;

import com.thiago.desafio_estagio.dto.ClientePjCreateDto;
import com.thiago.desafio_estagio.dto.ClientePjDto;
import com.thiago.desafio_estagio.dto.ClientePjUpdateDto;
import com.thiago.desafio_estagio.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.exceptions.CnpjJaCadastradoException;
import com.thiago.desafio_estagio.exceptions.EmailJaCadastradoException;
import com.thiago.desafio_estagio.exceptions.RazaoSocialJaCadastradaException;
import com.thiago.desafio_estagio.models.ClientePj;
import com.thiago.desafio_estagio.models.TipoPessoa;
import com.thiago.desafio_estagio.repository.ClientePjRepository;
import com.thiago.desafio_estagio.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientePjService {

    private final ClientePjRepository clientePjRepository;
    private final ClienteRepository clienteRepository;

    //Cria um novo Cliente PJ validando que não existe outro email igual já cadastrado na tabela Cliente
    //CNPJ é normalizado garantindo que mesmo que os clientes enviem com mascara o dado seja armazenado da maneira correta.
    @Transactional
    public ClientePjDto criar(ClientePjCreateDto dto) {
        String cnpj = dto.cnpj().replaceAll("[^0-9]", "");

        if (clienteRepository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException();
        }
        if (clientePjRepository.existsByCnpj(cnpj)) {
            throw new CnpjJaCadastradoException();
        }
        if (clientePjRepository.existsByRazaoSocial(dto.razaoSocial())) {
            throw new RazaoSocialJaCadastradaException();
        }

        ClientePj clientePj = new ClientePj();
        clientePj.setTipoPessoa(TipoPessoa.JURIDICA);
        clientePj.setEmail(dto.email());
        clientePj.setCnpj(cnpj);
        clientePj.setRazaoSocial(dto.razaoSocial());
        clientePj.setInscricaoEstadual(dto.inscricaoEstadual());
        clientePj.setDataCriacao(dto.dataCriacao());

        return ClientePjDto.from(clientePjRepository.save(clientePj), List.of());
    }

    @Transactional
    public ClientePjDto atualizar(UUID id, ClientePjUpdateDto dto) {
        ClientePj clientePj = clientePjRepository.findById(id)
                .orElseThrow(ClienteNaoEncontradoException::new);

        if (dto.email() != null && !dto.email().equals(clientePj.getEmail())) {
            if (clienteRepository.existsByEmailAndIdNot(dto.email(), id)) {
                throw new EmailJaCadastradoException();
            }
            clientePj.setEmail(dto.email());
        }

        // Razao social tambem precisa ser unica: rejeita se outro cliente ja a usa.
        if (dto.razaoSocial() != null && !dto.razaoSocial().equals(clientePj.getRazaoSocial())) {
            if (clientePjRepository.existsByRazaoSocial(dto.razaoSocial())) {
                throw new RazaoSocialJaCadastradaException();
            }
            clientePj.setRazaoSocial(dto.razaoSocial());
        }

        if (dto.inscricaoEstadual() != null) {
            clientePj.setInscricaoEstadual(dto.inscricaoEstadual());
        }

        if (dto.ativo() != null) {
            clientePj.setAtivo(dto.ativo());
        }

        return ClientePjDto.from(clientePjRepository.save(clientePj), List.of());
    }
}
