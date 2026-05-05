package com.thiago.desafio_estagio.service;

import com.thiago.desafio_estagio.dto.ClienteListDto;
import com.thiago.desafio_estagio.models.Cliente;
import com.thiago.desafio_estagio.models.ClientePf;
import com.thiago.desafio_estagio.models.ClientePj;
import com.thiago.desafio_estagio.models.TipoPessoa;
import com.thiago.desafio_estagio.repository.ClienteRepository;
import com.thiago.desafio_estagio.repository.ClienteSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Page<ClienteListDto> listarTodos(TipoPessoa tipoPessoa, String documento, String nome, Pageable pageable) {
        Specification<Cliente> spec = ClienteSpecification.comFiltros(tipoPessoa, documento, nome);
        return clienteRepository.findAll(spec, pageable).map(this::toDto);
    }

    private ClienteListDto toDto(Cliente cliente) {
        if (cliente instanceof ClientePf pf) {
            return new ClienteListDto(
                    pf.getId(), pf.getTipoPessoa(), pf.getEmail(), pf.isAtivo(),
                    pf.getCriadoEm(), pf.getAtualizadoEm(),
                    pf.getNome(), pf.getCpf(), pf.getRg(), pf.getDataNascimento(),
                    null, null, null, null
            );
        }

        ClientePj pj = (ClientePj) cliente;
        return new ClienteListDto(
                pj.getId(), pj.getTipoPessoa(), pj.getEmail(), pj.isAtivo(),
                pj.getCriadoEm(), pj.getAtualizadoEm(),
                null, null, null, null,
                pj.getCnpj(), pj.getRazaoSocial(), pj.getInscricaoEstatual(), pj.getDataCriacao()
        );
    }
}
