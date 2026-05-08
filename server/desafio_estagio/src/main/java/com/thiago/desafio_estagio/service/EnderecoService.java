package com.thiago.desafio_estagio.service;

import com.thiago.desafio_estagio.dto.EnderecoCreateDto;
import com.thiago.desafio_estagio.dto.EnderecoDto;
import com.thiago.desafio_estagio.dto.EnderecoUpdateDto;
import com.thiago.desafio_estagio.exceptions.CepJaCadastradoException;
import com.thiago.desafio_estagio.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.exceptions.EnderecoNaoEncontradoException;
import com.thiago.desafio_estagio.exceptions.EnderecoPrincipalException;
import com.thiago.desafio_estagio.models.Cliente;
import com.thiago.desafio_estagio.models.Endereco;
import com.thiago.desafio_estagio.repository.ClienteRepository;
import com.thiago.desafio_estagio.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;

    // Cadastra um novo endereco para o cliente informado pelo id
    // Garante que somente um endereço é o principal, desmarcando caso encontre um endereço principal existente
    @Transactional
    public EnderecoDto criar(UUID clienteId, EnderecoCreateDto dto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(ClienteNaoEncontradoException::new);

        if (enderecoRepository.existsByClienteIdAndCep(clienteId, dto.cep())) {
            throw new CepJaCadastradoException();
        }

        boolean principal = Boolean.TRUE.equals(dto.enderecoPrincipal());
        if (principal) {
            enderecoRepository.desmarcarTodosPrincipaisDoCliente(clienteId);
        }

        Endereco endereco = new Endereco();
        endereco.setLogradouro(dto.logradouro());
        endereco.setNumero(dto.numero());
        endereco.setCep(dto.cep());
        endereco.setBairro(dto.bairro());
        endereco.setTelefone(dto.telefone());
        endereco.setCidade(dto.cidade());
        endereco.setEstado(dto.estado());
        endereco.setEnderecoPrincipal(principal);
        endereco.setComplemento(dto.complemento());
        endereco.setCliente(cliente);

        return EnderecoDto.from(enderecoRepository.save(endereco));
    }

    @Transactional
    public EnderecoDto atualizar(UUID enderecoId, EnderecoUpdateDto dto) {
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(EnderecoNaoEncontradoException::new);

        if (dto.numero() != null) {
            endereco.setNumero(dto.numero());
        }

        if (dto.telefone() != null) {
            endereco.setTelefone(dto.telefone());
        }

        if (dto.complemento() != null) {
            endereco.setComplemento(dto.complemento());
        }

        if (dto.enderecoPrincipal() != null) {
            boolean tornarPrincipal = Boolean.TRUE.equals(dto.enderecoPrincipal());
            // Bloqueia desmarcar o endereço principal diretamente — sempre deve existir um
            // principal por cliente. Para trocar, marca-se outro endereço como principal.
            if (!tornarPrincipal && endereco.isEnderecoPrincipal()) {
                throw new EnderecoPrincipalException();
            }
            if (tornarPrincipal && !endereco.isEnderecoPrincipal()) {
                enderecoRepository.desmarcarTodosPrincipaisDoCliente(endereco.getCliente().getId());
            }
            endereco.setEnderecoPrincipal(tornarPrincipal);
        }

        return EnderecoDto.from(enderecoRepository.save(endereco));
    }

    @Transactional
    public void deletar(UUID enderecoId) {
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(EnderecoNaoEncontradoException::new);

        // Não permite deletar o endereço principal — o cliente precisa sempre ter um.
        if (endereco.isEnderecoPrincipal()) {
            throw new EnderecoPrincipalException();
        }

        enderecoRepository.delete(endereco);
    }
}
