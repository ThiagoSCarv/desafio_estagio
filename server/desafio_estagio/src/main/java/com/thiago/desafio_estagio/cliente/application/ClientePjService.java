package com.thiago.desafio_estagio.cliente.application;

import com.thiago.desafio_estagio.cliente.domain.ClientePj;
import com.thiago.desafio_estagio.cliente.domain.ClientePjRepository;
import com.thiago.desafio_estagio.cliente.domain.ClienteRepository;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.CnpjJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.EmailJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.RazaoSocialJaCadastradaException;
import com.thiago.desafio_estagio.endereco.application.EnderecoCreateDto;
import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
import com.thiago.desafio_estagio.endereco.domain.Endereco;
import com.thiago.desafio_estagio.endereco.domain.EnderecoRepository;
import com.thiago.desafio_estagio.endereco.domain.exceptions.EnderecoPrincipalException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class ClientePjService {

    private final ClientePjRepository clientePjRepository;
    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;

    //Cria um novo Cliente PJ validando que não existe outro email igual já cadastrado na tabela Cliente
    //CNPJ é normalizado garantindo que mesmo que os clientes enviem com mascara o dado seja armazenado da maneira correta.
    @Transactional
    public ClientePjDto criar(@Valid ClientePjCreateDto dto) {
        String cnpj = dto.cnpj().replaceAll("\\D", "");

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

        ClientePj clientePjSalvo = clientePjRepository.save(clientePj);
        List<EnderecoDto> enderecos = salvarEnderecos(clientePjSalvo, dto.enderecos());
        return ClientePjDto.from(clientePjSalvo, enderecos);
    }

    @Transactional
    public ClientePjDto atualizar(UUID id, @Valid ClientePjUpdateDto dto) {
        ClientePj clientePj = clientePjRepository.findById(id)
                .orElseThrow(ClienteNaoEncontradoException::new);

        if (dto.email() != null && !dto.email().equals(clientePj.getEmail())) {
            if (clienteRepository.existsByEmailAndIdNot(dto.email(), id)) throw new EmailJaCadastradoException();
            clientePj.setEmail(dto.email());
        }

        // Razao social tambem precisa ser unica: rejeita se outro cliente ja a usa.
        if (dto.razaoSocial() != null && !dto.razaoSocial().equals(clientePj.getRazaoSocial())) {
            if (clientePjRepository.existsByRazaoSocial(dto.razaoSocial())) throw new RazaoSocialJaCadastradaException();
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

    // Salva a lista de endereços vinculada ao cliente recém-criado.
    // Exatamente um endereço pode ser marcado como principal; se nenhum estiver marcado, o primeiro é principal.
    private List<EnderecoDto> salvarEnderecos(ClientePj cliente, List<EnderecoCreateDto> enderecos) {
        if (enderecos == null || enderecos.isEmpty()) {
            return List.of();
        }

        long quantidadePrincipais = enderecos.stream().filter(e -> Boolean.TRUE.equals(e.enderecoPrincipal())).count();
        if (quantidadePrincipais > 1) {
            throw new EnderecoPrincipalException("Apenas um endereço pode ser marcado como principal.");
        }

        int indicePrincipal = IntStream.range(0, enderecos.size())
                .filter(i -> Boolean.TRUE.equals(enderecos.get(i).enderecoPrincipal()))
                .findFirst()
                .orElse(0);

        List<Endereco> enderecosParaSalvar = new ArrayList<>();
        for (int i = 0; i < enderecos.size(); i++) {
            EnderecoCreateDto dto = enderecos.get(i);
            Endereco endereco = new Endereco();
            endereco.setLogradouro(dto.logradouro());
            endereco.setNumero(dto.numero());
            endereco.setCep(dto.cep());
            endereco.setBairro(dto.bairro());
            endereco.setTelefone(dto.telefone());
            endereco.setCidade(dto.cidade());
            endereco.setEstado(dto.estado());
            endereco.setEnderecoPrincipal(i == indicePrincipal);
            endereco.setComplemento(dto.complemento());
            endereco.setCliente(cliente);
            enderecosParaSalvar.add(endereco);
        }

        return enderecoRepository.saveAll(enderecosParaSalvar).stream()
                .map(EnderecoDto::from)
                .toList();
    }
}
