package com.thiago.desafio_estagio.cliente.application;

import com.thiago.desafio_estagio.cliente.domain.ClientePj;
import com.thiago.desafio_estagio.cliente.domain.ClientePjRepository;
import com.thiago.desafio_estagio.cliente.domain.ClienteRepository;
import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.CnpjJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.EmailJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.RazaoSocialJaCadastradaException;
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
class ClientePjServiceTest {

    @Mock
    private ClientePjRepository clientePjRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @InjectMocks
    private ClientePjService clientePjService;

    // --- criar ---

    @Test
    void criar_devePersistirCnpjSemMascara() {
        ClientePjCreateDto dto = createDto("11.222.333/0001-81", "Empresa SA");
        when(clientePjRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        clientePjService.criar(dto);

        ArgumentCaptor<ClientePj> captor = ArgumentCaptor.forClass(ClientePj.class);
        verify(clientePjRepository).save(captor.capture());
        assertThat(captor.getValue().getCnpj()).isEqualTo("11222333000181");
    }

    @Test
    void criar_emailDuplicado_deveLancarEmailJaCadastrado() {
        when(clienteRepository.existsByEmail("empresa@email.com")).thenReturn(true);

        assertThatThrownBy(() -> clientePjService.criar(createDto("11222333000181", "Empresa SA")))
                .isInstanceOf(EmailJaCadastradoException.class);

        verify(clientePjRepository, never()).save(any());
    }

    @Test
    void criar_cnpjDuplicado_deveLancarCnpjJaCadastrado() {
        when(clienteRepository.existsByEmail(any())).thenReturn(false);
        when(clientePjRepository.existsByCnpj("11222333000181")).thenReturn(true);

        assertThatThrownBy(() -> clientePjService.criar(createDto("11222333000181", "Empresa SA")))
                .isInstanceOf(CnpjJaCadastradoException.class);

        verify(clientePjRepository, never()).save(any());
    }

    @Test
    void criar_razaoSocialDuplicada_deveLancarRazaoSocialJaCadastrada() {
        when(clienteRepository.existsByEmail(any())).thenReturn(false);
        when(clientePjRepository.existsByCnpj(any())).thenReturn(false);
        when(clientePjRepository.existsByRazaoSocial("Empresa SA")).thenReturn(true);

        assertThatThrownBy(() -> clientePjService.criar(createDto("11222333000181", "Empresa SA")))
                .isInstanceOf(RazaoSocialJaCadastradaException.class);

        verify(clientePjRepository, never()).save(any());
    }

    @Test
    void criar_dadosValidos_deveRetornarDtoComCnpjNormalizado() {
        ClientePjCreateDto dto = createDto("11.222.333/0001-81", "Empresa SA");
        when(clientePjRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ClientePjDto resultado = clientePjService.criar(dto);

        assertThat(resultado.cnpj()).isEqualTo("11222333000181");
        assertThat(resultado.email()).isEqualTo("empresa@email.com");
        assertThat(resultado.enderecos()).isEmpty();
    }

    // --- atualizar ---

    @Test
    void atualizar_clienteNaoEncontrado_deveLancarClienteNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(clientePjRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientePjService.atualizar(id, new ClientePjUpdateDto(null, null, null, null)))
                .isInstanceOf(ClienteNaoEncontradoException.class);
    }

    @Test
    void atualizar_emailIgualAoAtual_naoDeveVerificarDuplicidade() {
        UUID id = UUID.randomUUID();
        ClientePj pj = clientePjExistente(id, "empresa@email.com", "Empresa SA");
        when(clientePjRepository.findById(id)).thenReturn(Optional.of(pj));
        when(clientePjRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        clientePjService.atualizar(id, new ClientePjUpdateDto("empresa@email.com", null, null, null));

        verify(clienteRepository, never()).existsByEmailAndIdNot(any(), any());
    }

    @Test
    void atualizar_novoEmailJaUsadoPorOutro_deveLancarEmailJaCadastrado() {
        UUID id = UUID.randomUUID();
        ClientePj pj = clientePjExistente(id, "empresa@email.com", "Empresa SA");
        when(clientePjRepository.findById(id)).thenReturn(Optional.of(pj));
        when(clienteRepository.existsByEmailAndIdNot("outro@email.com", id)).thenReturn(true);

        assertThatThrownBy(() -> clientePjService.atualizar(id, new ClientePjUpdateDto("outro@email.com", null, null, null)))
                .isInstanceOf(EmailJaCadastradoException.class);

        verify(clientePjRepository, never()).save(any());
    }

    @Test
    void atualizar_razaoSocialIgualAAtual_naoDeveVerificarDuplicidade() {
        UUID id = UUID.randomUUID();
        ClientePj pj = clientePjExistente(id, "empresa@email.com", "Empresa SA");
        when(clientePjRepository.findById(id)).thenReturn(Optional.of(pj));
        when(clientePjRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        clientePjService.atualizar(id, new ClientePjUpdateDto(null, "Empresa SA", null, null));

        verify(clientePjRepository, never()).existsByRazaoSocial(any());
    }

    @Test
    void atualizar_novaRazaoSocialJaUsadaPorOutro_deveLancarRazaoSocialJaCadastrada() {
        UUID id = UUID.randomUUID();
        ClientePj pj = clientePjExistente(id, "empresa@email.com", "Empresa SA");
        when(clientePjRepository.findById(id)).thenReturn(Optional.of(pj));
        when(clientePjRepository.existsByRazaoSocial("Outra Empresa LTDA")).thenReturn(true);

        assertThatThrownBy(() -> clientePjService.atualizar(id, new ClientePjUpdateDto(null, "Outra Empresa LTDA", null, null)))
                .isInstanceOf(RazaoSocialJaCadastradaException.class);

        verify(clientePjRepository, never()).save(any());
    }

    @Test
    void atualizar_camposParciais_deveAtualizarApenasOsInformados() {
        UUID id = UUID.randomUUID();
        ClientePj pj = clientePjExistente(id, "empresa@email.com", "Empresa SA");
        when(clientePjRepository.findById(id)).thenReturn(Optional.of(pj));
        when(clientePjRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        clientePjService.atualizar(id, new ClientePjUpdateDto(null, null, "IE-123456", false));

        assertThat(pj.getInscricaoEstadual()).isEqualTo("IE-123456");
        assertThat(pj.isAtivo()).isFalse();
        assertThat(pj.getRazaoSocial()).isEqualTo("Empresa SA"); // não mudou
        assertThat(pj.getEmail()).isEqualTo("empresa@email.com"); // não mudou
    }

    // --- helpers ---

    private ClientePjCreateDto createDto(String cnpj, String razaoSocial) {
        return new ClientePjCreateDto("empresa@email.com", cnpj, razaoSocial, null, LocalDate.of(2010, 3, 15), null);
    }

    private ClientePj clientePjExistente(UUID id, String email, String razaoSocial) {
        ClientePj pj = new ClientePj();
        pj.setId(id);
        pj.setEmail(email);
        pj.setCnpj("11222333000181");
        pj.setRazaoSocial(razaoSocial);
        pj.setDataCriacao(LocalDate.of(2010, 3, 15));
        return pj;
    }
}
