package com.thiago.desafio_estagio.endereco.domain;

import com.thiago.desafio_estagio.cliente.domain.ClientePf;
import com.thiago.desafio_estagio.cliente.domain.ClientePfRepository;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Substitui o dialeto MySQL pelo H2 para o banco embarcado de testes
@DataJpaTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
class EnderecoRepositoryTest {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ClientePfRepository clientePfRepository;

    @Autowired
    private TestEntityManager em;

    // --- existsByClienteId ---

    @Test
    void existsByClienteId_semEnderecos_retornaFalse() {
        ClientePf pf = clientePfExistente("a@email.com", "52998224725");
        clientePfRepository.save(pf);

        assertThat(enderecoRepository.existsByClienteId(pf.getId())).isFalse();
    }

    @Test
    void existsByClienteId_comEndereco_retornaTrue() {
        ClientePf pf = clientePfExistente("a@email.com", "52998224725");
        clientePfRepository.save(pf);
        enderecoRepository.save(endereco(pf, true));

        assertThat(enderecoRepository.existsByClienteId(pf.getId())).isTrue();
    }

    @Test
    void existsByClienteId_retornaFalseParaIdInexistente() {
        assertThat(enderecoRepository.existsByClienteId(UUID.randomUUID())).isFalse();
    }

    // --- findByClienteId ---

    @Test
    void findByClienteId_retornaApenasEnderecosDessaCliente() {
        ClientePf pfA = clientePfExistente("a@email.com", "52998224725");
        ClientePf pfB = clientePfExistente("b@email.com", "12345678909");
        clientePfRepository.save(pfA);
        clientePfRepository.save(pfB);

        enderecoRepository.save(endereco(pfA, true));
        enderecoRepository.save(endereco(pfA, false));
        enderecoRepository.save(endereco(pfB, true));

        List<Endereco> resultados = enderecoRepository.findByClienteId(pfA.getId());

        assertThat(resultados).hasSize(2);
        assertThat(resultados).allMatch(e -> e.getCliente().getId().equals(pfA.getId()));
    }

    @Test
    void findByClienteId_clienteSemEnderecos_retornaListaVazia() {
        ClientePf pf = clientePfExistente("a@email.com", "52998224725");
        clientePfRepository.save(pf);

        assertThat(enderecoRepository.findByClienteId(pf.getId())).isEmpty();
    }

    // --- desmarcarTodosPrincipaisDoCliente ---

    @Test
    void desmarcarTodos_defineEnderecoPrincipalFalseParaTodosDoCliente() {
        ClientePf pf = clientePfExistente("a@email.com", "52998224725");
        clientePfRepository.save(pf);

        Endereco e1 = enderecoRepository.save(endereco(pf, true));
        Endereco e2 = enderecoRepository.save(endereco(pf, true));
        em.flush();
        em.clear();

        enderecoRepository.desmarcarTodosPrincipaisDoCliente(pf.getId());
        em.flush();
        em.clear();

        assertThat(enderecoRepository.findById(e1.getId()).orElseThrow().isEnderecoPrincipal()).isFalse();
        assertThat(enderecoRepository.findById(e2.getId()).orElseThrow().isEnderecoPrincipal()).isFalse();
    }

    @Test
    void desmarcarTodos_naoAfetaEnderecoDeOutroCliente() {
        ClientePf pfA = clientePfExistente("a@email.com", "52998224725");
        ClientePf pfB = clientePfExistente("b@email.com", "12345678909");
        clientePfRepository.save(pfA);
        clientePfRepository.save(pfB);

        enderecoRepository.save(endereco(pfA, true));
        Endereco enderecoB = enderecoRepository.save(endereco(pfB, true));
        em.flush();
        em.clear();

        // desmarca somente os endereços de pfA
        enderecoRepository.desmarcarTodosPrincipaisDoCliente(pfA.getId());
        em.flush();
        em.clear();

        // endereço de pfB deve continuar principal
        assertThat(enderecoRepository.findById(enderecoB.getId()).orElseThrow().isEnderecoPrincipal()).isTrue();
    }

    @Test
    void desmarcarTodos_enderecoNaoPrincipal_permaneceNaoPrincipal() {
        ClientePf pf = clientePfExistente("a@email.com", "52998224725");
        clientePfRepository.save(pf);

        Endereco naoPrincipal = enderecoRepository.save(endereco(pf, false));
        em.flush();
        em.clear();

        enderecoRepository.desmarcarTodosPrincipaisDoCliente(pf.getId());
        em.flush();
        em.clear();

        assertThat(enderecoRepository.findById(naoPrincipal.getId()).orElseThrow().isEnderecoPrincipal()).isFalse();
    }

    // --- helpers ---

    private ClientePf clientePfExistente(String email, String cpf) {
        ClientePf pf = new ClientePf();
        pf.setTipoPessoa(TipoPessoa.FISICA);
        pf.setEmail(email);
        pf.setNome("João Silva");
        pf.setCpf(cpf);
        pf.setRg("RG" + cpf.substring(0, 5));
        pf.setDataNascimento(LocalDate.of(1990, 1, 1));
        return pf;
    }

    private Endereco endereco(ClientePf cliente, boolean principal) {
        Endereco e = new Endereco();
        e.setLogradouro("Rua A");
        e.setNumero("100");
        e.setCep("01310100");
        e.setBairro("Centro");
        e.setCidade("São Paulo");
        e.setEstado("SP");
        e.setEnderecoPrincipal(principal);
        e.setCliente(cliente);
        return e;
    }
}
