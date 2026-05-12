package com.thiago.desafio_estagio.cliente.application;

import com.thiago.desafio_estagio.cliente.domain.ClientePf;
import com.thiago.desafio_estagio.cliente.domain.ClientePfRepository;
import com.thiago.desafio_estagio.cliente.domain.ClienteRepository;
import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.CpfJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.EmailJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.RgJaCadastradoException;
import com.thiago.desafio_estagio.endereco.domain.EnderecoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientePfServiceTest {

    @Mock
    private ClientePfRepository clientePfRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @InjectMocks
    private ClientePfService clientePfService;

    // --- criar ---

    @Test
    void criar_devePersistirCpfSemMascara() {
        ClientePfCreateDto dto = createDto("529.982.247-25", "1234567");
        when(clientePfRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        clientePfService.criar(dto);

        ArgumentCaptor<ClientePf> captor = ArgumentCaptor.forClass(ClientePf.class);
        verify(clientePfRepository).save(captor.capture());
        assertThat(captor.getValue().getCpf()).isEqualTo("52998224725");
    }

    @Test
    void criar_devePersistirRgNormalizadoSemMascaraEmMaiusculo() {
        // RG com pontos, hífen e letras minúsculas
        ClientePfCreateDto dto = createDto("52998224725", "mg-12.345");
        when(clientePfRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        clientePfService.criar(dto);

        ArgumentCaptor<ClientePf> captor = ArgumentCaptor.forClass(ClientePf.class);
        verify(clientePfRepository).save(captor.capture());
        assertThat(captor.getValue().getRg()).isEqualTo("MG12345");
    }

    @Test
    void criar_emailDuplicado_deveLancarEmailJaCadastrado() {
        when(clienteRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> clientePfService.criar(createDto("52998224725", "1234567")))
                .isInstanceOf(EmailJaCadastradoException.class);

        verify(clientePfRepository, never()).save(any());
    }

    @Test
    void criar_cpfDuplicado_deveLancarCpfJaCadastrado() {
        when(clienteRepository.existsByEmail(any())).thenReturn(false);
        when(clientePfRepository.existsByCpf("52998224725")).thenReturn(true);

        assertThatThrownBy(() -> clientePfService.criar(createDto("52998224725", "1234567")))
                .isInstanceOf(CpfJaCadastradoException.class);

        verify(clientePfRepository, never()).save(any());
    }

    @Test
    void criar_rgDuplicado_deveLancarRgJaCadastrado() {
        when(clienteRepository.existsByEmail(any())).thenReturn(false);
        when(clientePfRepository.existsByCpf(any())).thenReturn(false);
        when(clientePfRepository.existsByRg("1234567")).thenReturn(true);

        assertThatThrownBy(() -> clientePfService.criar(createDto("52998224725", "1234567")))
                .isInstanceOf(RgJaCadastradoException.class);

        verify(clientePfRepository, never()).save(any());
    }

    @Test
    void criar_dadosValidos_deveRetornarDtoComEmailCorreto() {
        ClientePfCreateDto dto = createDto("52998224725", "1234567");
        when(clientePfRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ClientePfDto resultado = clientePfService.criar(dto);

        assertThat(resultado.email()).isEqualTo("joao@email.com");
        assertThat(resultado.enderecos()).isEmpty();
    }

    // --- atualizar ---

    @Test
    void atualizar_clienteNaoEncontrado_deveLancarClienteNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(clientePfRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientePfService.atualizar(id, new ClientePfUpdateDto(null, null, null)))
                .isInstanceOf(ClienteNaoEncontradoException.class);
    }

    @Test
    void atualizar_emailIgualAoAtual_naoDeveVerificarDuplicidade() {
        UUID id = UUID.randomUUID();
        ClientePf pf = clientePfExistente(id, "joao@email.com");
        when(clientePfRepository.findById(id)).thenReturn(Optional.of(pf));
        when(clientePfRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // mesmo email → o bloco de verificação deve ser pulado
        clientePfService.atualizar(id, new ClientePfUpdateDto("joao@email.com", null, null));

        verify(clienteRepository, never()).existsByEmailAndIdNot(any(), any());
    }

    @Test
    void atualizar_novoEmailJaUsadoPorOutro_deveLancarEmailJaCadastrado() {
        UUID id = UUID.randomUUID();
        ClientePf pf = clientePfExistente(id, "joao@email.com");
        when(clientePfRepository.findById(id)).thenReturn(Optional.of(pf));
        when(clienteRepository.existsByEmailAndIdNot("novo@email.com", id)).thenReturn(true);

        assertThatThrownBy(() -> clientePfService.atualizar(id, new ClientePfUpdateDto("novo@email.com", null, null)))
                .isInstanceOf(EmailJaCadastradoException.class);

        verify(clientePfRepository, never()).save(any());
    }

    @Test
    void atualizar_camposParciais_deveAtualizarApenasOsInformados() {
        UUID id = UUID.randomUUID();
        ClientePf pf = clientePfExistente(id, "joao@email.com");
        pf.setNome("João");
        when(clientePfRepository.findById(id)).thenReturn(Optional.of(pf));
        when(clientePfRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        clientePfService.atualizar(id, new ClientePfUpdateDto(null, "João Atualizado", false));

        assertThat(pf.getNome()).isEqualTo("João Atualizado");
        assertThat(pf.isAtivo()).isFalse();
        assertThat(pf.getEmail()).isEqualTo("joao@email.com"); // email não mudou
    }

    // --- helpers ---

    private ClientePfCreateDto createDto(String cpf, String rg) {
        return new ClientePfCreateDto("joao@email.com", "João Silva", cpf, rg, LocalDate.of(1990, 1, 1), null);
    }

    private ClientePf clientePfExistente(UUID id, String email) {
        ClientePf pf = new ClientePf();
        pf.setId(id);
        pf.setEmail(email);
        pf.setNome("João");
        pf.setCpf("52998224725");
        pf.setRg("1234567");
        pf.setDataNascimento(LocalDate.of(1990, 1, 1));
        return pf;
    }
}
