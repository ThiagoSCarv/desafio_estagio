package com.thiago.desafio_estagio.cliente.application;

import com.thiago.desafio_estagio.cliente.domain.ClientePf;
import com.thiago.desafio_estagio.cliente.domain.ClientePfRepository;
import com.thiago.desafio_estagio.cliente.domain.ClienteRepository;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.CpfJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.EmailJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.RgJaCadastradoException;
import com.thiago.desafio_estagio.endereco.application.EnderecoCreateDto;
import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
import com.thiago.desafio_estagio.endereco.domain.Endereco;
import com.thiago.desafio_estagio.endereco.domain.EnderecoRepository;
import com.thiago.desafio_estagio.endereco.domain.exceptions.EnderecoPrincipalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientePfService {

    private final ClientePfRepository clientePfRepository;
    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;

    //Cria um novo Cliente PF validando que não existe outro email igual já cadastrado na tabela Cliente
    //CPF e RG são normalizados garantindo que mesmo que os clientes enviem com mascara o dado seja armazenado da maneira correta.
    @Transactional
    public ClientePfDto criar(ClientePfCreateDto dto) {
        String cpf = dto.cpf().replaceAll("\\D", "");
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

        ClientePf saved = clientePfRepository.save(clientePf);
        List<EnderecoDto> enderecos = salvarEnderecos(saved, dto.enderecos());
        return ClientePfDto.from(saved, enderecos);
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

    // Salva a lista de endereços vinculada ao cliente recém-criado.
    // Exatamente um endereço pode ser marcado como principal; se nenhum estiver marcado, o primeiro é principal.
    private List<EnderecoDto> salvarEnderecos(ClientePf cliente, List<EnderecoCreateDto> enderecos) {
        if (enderecos == null || enderecos.isEmpty()) {
            return List.of();
        }

        long qtdPrincipais = enderecos.stream().filter(e -> Boolean.TRUE.equals(e.enderecoPrincipal())).count();
        if (qtdPrincipais > 1) {
            throw new EnderecoPrincipalException("Apenas um endereço pode ser marcado como principal.");
        }

        int principalIdx = 0;
        for (int i = 0; i < enderecos.size(); i++) {
            if (Boolean.TRUE.equals(enderecos.get(i).enderecoPrincipal())) {
                principalIdx = i;
                break;
            }
        }

        List<Endereco> entities = new ArrayList<>();
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
            endereco.setEnderecoPrincipal(i == principalIdx);
            endereco.setComplemento(dto.complemento());
            endereco.setCliente(cliente);
            entities.add(endereco);
        }

        return enderecoRepository.saveAll(entities).stream()
                .map(EnderecoDto::from)
                .toList();
    }
}
