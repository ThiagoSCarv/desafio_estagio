package com.thiago.desafio_estagio.cliente.application;

import com.thiago.desafio_estagio.cliente.domain.Cliente;
import com.thiago.desafio_estagio.cliente.domain.ClientePf;
import com.thiago.desafio_estagio.cliente.domain.ClientePj;
import com.thiago.desafio_estagio.cliente.domain.ClienteRepository;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.endereco.domain.EnderecoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @InjectMocks
    private ClienteService clienteService;

    // --- buscarPorId ---

    @Test
    void buscarPorId_clienteNaoEncontrado_deveLancarClienteNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(id))
                .isInstanceOf(ClienteNaoEncontradoException.class);

        verifyNoInteractions(enderecoRepository);
    }

    @Test
    void buscarPorId_clientePf_deveRetornarClientePfDtoComEnderecos() {
        UUID id = UUID.randomUUID();
        ClientePf pf = clientePfExistente(id);
        when(clienteRepository.findById(id)).thenReturn(Optional.of(pf));
        when(enderecoRepository.findByClienteId(id)).thenReturn(List.of());

        ClienteDto resultado = clienteService.buscarPorId(id);

        assertThat(resultado).isInstanceOf(ClientePfDto.class);
        assertThat(((ClientePfDto) resultado).cpf()).isEqualTo("52998224725");
        assertThat(resultado.enderecos()).isEmpty();
    }

    @Test
    void buscarPorId_clientePj_deveRetornarClientePjDto() {
        UUID id = UUID.randomUUID();
        ClientePj pj = clientePjExistente(id);
        when(clienteRepository.findById(id)).thenReturn(Optional.of(pj));
        when(enderecoRepository.findByClienteId(id)).thenReturn(List.of());

        ClienteDto resultado = clienteService.buscarPorId(id);

        assertThat(resultado).isInstanceOf(ClientePjDto.class);
        assertThat(((ClientePjDto) resultado).cnpj()).isEqualTo("11222333000181");
    }

    // --- deletar ---

    @Test
    void deletar_clienteNaoEncontrado_deveLancarClienteNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.deletar(id))
                .isInstanceOf(ClienteNaoEncontradoException.class);

        verify(clienteRepository, never()).delete(any(Cliente.class));
    }

    @Test
    void deletar_clienteExistente_deveChamarDelete() {
        UUID id = UUID.randomUUID();
        ClientePf pf = clientePfExistente(id);
        when(clienteRepository.findById(id)).thenReturn(Optional.of(pf));

        clienteService.deletar(id);

        verify(clienteRepository).delete(any(Cliente.class));
    }

    // --- listarTodos ---

    @Test
    void listarTodos_deveDelegarParaRepositoryEMapearResultados() {
        ClientePf pf = clientePfExistente(UUID.randomUUID());
        Page<Cliente> page = new PageImpl<>(List.of(pf));
        when(clienteRepository.buscarComFiltros(any(), any(), any(), any(PageRequest.class))).thenReturn(page);

        PageRequest pageable = PageRequest.of(0, 10);
        Page<ClienteDto> resultado = clienteService.listarTodos(null, null, null, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0)).isInstanceOf(ClientePfDto.class);
    }

    @Test
    void listarTodos_semResultados_deveRetornarPaginaVazia() {
        when(clienteRepository.buscarComFiltros(eq(TipoPessoa.FISICA), any(), any(), any(PageRequest.class)))
                .thenReturn(Page.empty());

        Page<ClienteDto> resultado = clienteService.listarTodos(TipoPessoa.FISICA, null, null, PageRequest.of(0, 10));

        assertThat(resultado.getContent()).isEmpty();
    }

    // --- helpers ---

    private ClientePf clientePfExistente(UUID id) {
        ClientePf pf = new ClientePf();
        pf.setId(id);
        pf.setEmail("joao@email.com");
        pf.setTipoPessoa(TipoPessoa.FISICA);
        pf.setNome("João Silva");
        pf.setCpf("52998224725");
        pf.setRg("1234567");
        pf.setDataNascimento(LocalDate.of(1990, 1, 1));
        return pf;
    }

    private ClientePj clientePjExistente(UUID id) {
        ClientePj pj = new ClientePj();
        pj.setId(id);
        pj.setEmail("empresa@email.com");
        pj.setTipoPessoa(TipoPessoa.JURIDICA);
        pj.setCnpj("11222333000181");
        pj.setRazaoSocial("Empresa SA");
        pj.setDataCriacao(LocalDate.of(2010, 3, 15));
        return pj;
    }
}
