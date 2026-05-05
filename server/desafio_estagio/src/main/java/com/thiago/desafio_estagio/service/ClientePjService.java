package com.thiago.desafio_estagio.service;

import com.thiago.desafio_estagio.dto.ClientePjCreateDto;
import com.thiago.desafio_estagio.dto.ClientePjUpdateDto;
import com.thiago.desafio_estagio.dto.EnderecoCreateDto;
import com.thiago.desafio_estagio.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.exceptions.CnpjJaCadastradoException;
import com.thiago.desafio_estagio.exceptions.EmailJaCadastradoException;
import com.thiago.desafio_estagio.exceptions.RazaoSocialJaCadastradaException;
import com.thiago.desafio_estagio.models.ClientePj;
import com.thiago.desafio_estagio.models.Endereco;
import com.thiago.desafio_estagio.models.TipoPessoa;
import com.thiago.desafio_estagio.repository.ClientePjRepository;
import com.thiago.desafio_estagio.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClientePjService {

    @Autowired
    private ClientePjRepository clientePjRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    public ClientePj criar(ClientePjCreateDto dto) {
        if (clientePjRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaCadastradoException();
        }
        if (clientePjRepository.existsByCnpj(dto.getCnpj())) {
            throw new CnpjJaCadastradoException();
        }
        if (clientePjRepository.existsByRazaoSocial(dto.getRazaoSocial())) {
            throw new RazaoSocialJaCadastradaException();
        }

        ClientePj clientePj = new ClientePj();
        clientePj.setTipoPessoa(TipoPessoa.JURIDICA);
        clientePj.setEmail(dto.getEmail());
        clientePj.setCnpj(dto.getCnpj());
        clientePj.setRazaoSocial(dto.getRazaoSocial());
        clientePj.setInscricaoEstatual(dto.getInscricaoEstatual());
        clientePj.setDataCriacao(dto.getDataCriacao());

        if (dto.getEnderecos() != null) {
            List<Endereco> enderecos = dto.getEnderecos().stream()
                    .map(enderecoDto -> mapearEndereco(enderecoDto, clientePj))
                    .toList();
            clientePj.getEnderecos().addAll(enderecos);
        }

        return clientePjRepository.save(clientePj);
    }

    @Transactional
    public ClientePj atualizar(UUID id, ClientePjUpdateDto dto) {
        ClientePj clientePj = clientePjRepository.findById(id)
                .orElseThrow(ClienteNaoEncontradoException::new);

        if (dto.getEmail() != null && !dto.getEmail().equals(clientePj.getEmail())) {
            if (clienteRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
                throw new EmailJaCadastradoException();
            }
            clientePj.setEmail(dto.getEmail());
        }

        if (dto.getAtivo() != null) {
            clientePj.setAtivo(dto.getAtivo());
        }

        return clientePjRepository.save(clientePj);
    }

    private Endereco mapearEndereco(EnderecoCreateDto dto, ClientePj clientePj) {
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
        endereco.setCliente(clientePj);
        return endereco;
    }
}
