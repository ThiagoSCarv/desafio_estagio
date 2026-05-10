package com.thiago.desafio_estagio.endereco.application;

import com.thiago.desafio_estagio.cliente.domain.Cliente;
import com.thiago.desafio_estagio.cliente.domain.ClientePf;
import com.thiago.desafio_estagio.cliente.domain.ClienteRepository;
import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.endereco.domain.Endereco;
import com.thiago.desafio_estagio.endereco.domain.EnderecoRepository;
import com.thiago.desafio_estagio.endereco.domain.exceptions.EnderecoNaoEncontradoException;
import com.thiago.desafio_estagio.endereco.domain.exceptions.EnderecoPrincipalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnderecoServiceTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private EnderecoService enderecoService;

    private UUID clienteId;
    private UUID enderecoId;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        enderecoId = UUID.randomUUID();
        cliente = new ClientePf();
        cliente.setId(clienteId);
    }

    // --- criar ---

    @Test
    void criar_primeiroEndereco_deveSerSemprePrincipalIndependenteDoDto() {
        EnderecoCreateDto dto = enderecoCreateDto(false); // dto diz false, mas deve virar true
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(enderecoRepository.existsByClienteId(clienteId)).thenReturn(false);
        when(enderecoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        enderecoService.criar(clienteId, dto);

        ArgumentCaptor<Endereco> captor = ArgumentCaptor.forClass(Endereco.class);
        verify(enderecoRepository).save(captor.capture());
        assertThat(captor.getValue().isEnderecoPrincipal()).isTrue();
    }

    @Test
    void criar_primeiroEndereco_naoDeveChamarDesmarcarPrincipais() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(enderecoRepository.existsByClienteId(clienteId)).thenReturn(false);
        when(enderecoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        enderecoService.criar(clienteId, enderecoCreateDto(true));

        verify(enderecoRepository, never()).desmarcarTodosPrincipaisDoCliente(any());
    }

    @Test
    void criar_naoEhPrimeiro_eEnderecoPrincipalTrue_deveChamarDesmarcarEPersistirComoPrincipal() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(enderecoRepository.existsByClienteId(clienteId)).thenReturn(true);
        when(enderecoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        enderecoService.criar(clienteId, enderecoCreateDto(true));

        verify(enderecoRepository).desmarcarTodosPrincipaisDoCliente(clienteId);
        ArgumentCaptor<Endereco> captor = ArgumentCaptor.forClass(Endereco.class);
        verify(enderecoRepository).save(captor.capture());
        assertThat(captor.getValue().isEnderecoPrincipal()).isTrue();
    }

    @Test
    void criar_naoEhPrimeiro_eEnderecoPrincipalFalse_devePersistirComoNaoPrincipalSemDesmarcar() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(enderecoRepository.existsByClienteId(clienteId)).thenReturn(true);
        when(enderecoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        enderecoService.criar(clienteId, enderecoCreateDto(false));

        verify(enderecoRepository, never()).desmarcarTodosPrincipaisDoCliente(any());
        ArgumentCaptor<Endereco> captor = ArgumentCaptor.forClass(Endereco.class);
        verify(enderecoRepository).save(captor.capture());
        assertThat(captor.getValue().isEnderecoPrincipal()).isFalse();
    }

    @Test
    void criar_clienteNaoEncontrado_deveLancarClienteNaoEncontrado() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enderecoService.criar(clienteId, enderecoCreateDto(false)))
                .isInstanceOf(ClienteNaoEncontradoException.class);

        verifyNoInteractions(enderecoRepository);
    }

    // --- atualizar ---

    @Test
    void atualizar_enderecoNaoEncontrado_deveLancarEnderecoNaoEncontrado() {
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enderecoService.atualizar(enderecoId, new EnderecoUpdateDto(null, null, null, null)))
                .isInstanceOf(EnderecoNaoEncontradoException.class);
    }

    @Test
    void atualizar_tentativaDeDesmarcarPrincipal_deveLancarEnderecoPrincipal() {
        Endereco endereco = enderecoComPrincipal(true);
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.of(endereco));

        EnderecoUpdateDto dto = new EnderecoUpdateDto(null, null, false, null);

        assertThatThrownBy(() -> enderecoService.atualizar(enderecoId, dto))
                .isInstanceOf(EnderecoPrincipalException.class);

        verify(enderecoRepository, never()).desmarcarTodosPrincipaisDoCliente(any());
    }

    @Test
    void atualizar_marcaNaoComoPrincipalQuandoJaEhPrincipal_naoDeveLancarNemDesmarcar() {
        Endereco endereco = enderecoComPrincipal(true);
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.of(endereco));
        when(enderecoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // dto pede para virar principal, mas já é — deve apenas salvar sem desmarcar outros
        enderecoService.atualizar(enderecoId, new EnderecoUpdateDto(null, null, true, null));

        verify(enderecoRepository, never()).desmarcarTodosPrincipaisDoCliente(any());
        verify(enderecoRepository).save(endereco);
    }

    @Test
    void atualizar_marcaNovoPrincipal_deveChamarDesmarcarEPersistirComoPrincipal() {
        Endereco endereco = enderecoComPrincipal(false);
        endereco.setCliente(cliente);
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.of(endereco));
        when(enderecoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        enderecoService.atualizar(enderecoId, new EnderecoUpdateDto(null, null, true, null));

        verify(enderecoRepository).desmarcarTodosPrincipaisDoCliente(clienteId);
        assertThat(endereco.isEnderecoPrincipal()).isTrue();
    }

    @Test
    void atualizar_camposSimples_deveAtualizarNumeroTelefoneEComplemento() {
        Endereco endereco = enderecoComPrincipal(false);
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.of(endereco));
        when(enderecoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        enderecoService.atualizar(enderecoId, new EnderecoUpdateDto("200", "11987654321", null, "Apto 5"));

        assertThat(endereco.getNumero()).isEqualTo("200");
        assertThat(endereco.getTelefone()).isEqualTo("11987654321");
        assertThat(endereco.getComplemento()).isEqualTo("Apto 5");
        assertThat(endereco.isEnderecoPrincipal()).isFalse(); // não mudou
    }

    // --- deletar ---

    @Test
    void deletar_enderecoNaoEncontrado_deveLancarEnderecoNaoEncontrado() {
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enderecoService.deletar(enderecoId))
                .isInstanceOf(EnderecoNaoEncontradoException.class);

        verify(enderecoRepository, never()).delete(any());
    }

    @Test
    void deletar_enderecoPrincipal_deveLancarEnderecoPrincipal() {
        Endereco endereco = enderecoComPrincipal(true);
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.of(endereco));

        assertThatThrownBy(() -> enderecoService.deletar(enderecoId))
                .isInstanceOf(EnderecoPrincipalException.class);

        verify(enderecoRepository, never()).delete(any());
    }

    @Test
    void deletar_enderecoNaoPrincipal_deveRemover() {
        Endereco endereco = enderecoComPrincipal(false);
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.of(endereco));

        enderecoService.deletar(enderecoId);

        verify(enderecoRepository).delete(endereco);
    }

    // --- helpers ---

    private EnderecoCreateDto enderecoCreateDto(boolean principal) {
        return new EnderecoCreateDto(
                "Rua A", "100", "01310100", "Centro",
                null, "São Paulo", "SP", principal, null
        );
    }

    private Endereco enderecoComPrincipal(boolean principal) {
        Endereco e = new Endereco();
        e.setId(enderecoId);
        e.setLogradouro("Rua A");
        e.setNumero("100");
        e.setCep("01310100");
        e.setBairro("Centro");
        e.setCidade("São Paulo");
        e.setEstado("SP");
        e.setEnderecoPrincipal(principal);
        return e;
    }
}
