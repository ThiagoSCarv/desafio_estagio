package com.thiago.desafio_estagio.cliente.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Testa queries que cruzam a tabela pai clientes com as subtabelas clientes_pf e clientes_pj.
// O foco é garantir que existsByEmail e existsByEmailAndIdNot cubram ambos os tipos de pessoa.
@DataJpaTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClientePfRepository clientePfRepository;

    @Autowired
    private ClientePjRepository clientePjRepository;

    @Autowired
    private TestEntityManager em;

    // --- existsByEmail ---

    @Test
    void existsByEmail_encontraClientePf() {
        clientePfRepository.save(clientePf("pf@email.com", "52998224725", "1234567"));

        assertThat(clienteRepository.existsByEmail("pf@email.com")).isTrue();
    }

    @Test
    void existsByEmail_encontraClientePj() {
        // Valida que a query não filtra por discriminador — cobre PJ também
        clientePjRepository.save(clientePj("pj@email.com", "11222333000181", "Empresa SA"));

        assertThat(clienteRepository.existsByEmail("pj@email.com")).isTrue();
    }

    @Test
    void existsByEmail_emailInexistente_retornaFalse() {
        assertThat(clienteRepository.existsByEmail("ninguem@email.com")).isFalse();
    }

    @Test
    void existsByEmail_naoConfundeEmailsDiferentes() {
        clientePfRepository.save(clientePf("pf@email.com", "52998224725", "1234567"));

        assertThat(clienteRepository.existsByEmail("outro@email.com")).isFalse();
    }

    // --- existsByEmailAndIdNot ---

    @Test
    void existsByEmailAndIdNot_emailDoProprioCliente_retornaFalse() {
        // Cenário de auto-atualização: não deve bloquear o cliente de manter o próprio email
        ClientePf pf = clientePfRepository.save(clientePf("joao@email.com", "52998224725", "1234567"));
        em.flush();
        em.clear();

        assertThat(clienteRepository.existsByEmailAndIdNot("joao@email.com", pf.getId())).isFalse();
    }

    @Test
    void existsByEmailAndIdNot_emailDeOutroCliente_retornaTrue() {
        ClientePf pfA = clientePfRepository.save(clientePf("a@email.com", "52998224725", "1234567"));
        clientePfRepository.save(clientePf("b@email.com", "12345678909", "7654321"));
        em.flush();
        em.clear();

        // pfA tenta usar o email de pfB → deve ser bloqueado
        assertThat(clienteRepository.existsByEmailAndIdNot("b@email.com", pfA.getId())).isTrue();
    }

    @Test
    void existsByEmailAndIdNot_emailDeOutroPjPorPf_retornaTrue() {
        // Email registrado em PJ deve bloquear um PF de adotá-lo
        ClientePf pf = clientePfRepository.save(clientePf("pf@email.com", "52998224725", "1234567"));
        clientePjRepository.save(clientePj("pj@email.com", "11222333000181", "Empresa SA"));
        em.flush();
        em.clear();

        assertThat(clienteRepository.existsByEmailAndIdNot("pj@email.com", pf.getId())).isTrue();
    }

    @Test
    void existsByEmailAndIdNot_emailInexistente_retornaFalse() {
        assertThat(clienteRepository.existsByEmailAndIdNot("fantasma@email.com", UUID.randomUUID())).isFalse();
    }

    // --- helpers ---

    private ClientePf clientePf(String email, String cpf, String rg) {
        ClientePf pf = new ClientePf();
        pf.setTipoPessoa(TipoPessoa.FISICA);
        pf.setEmail(email);
        pf.setNome("João Silva");
        pf.setCpf(cpf);
        pf.setRg(rg);
        pf.setDataNascimento(LocalDate.of(1990, 1, 1));
        return pf;
    }

    private ClientePj clientePj(String email, String cnpj, String razaoSocial) {
        ClientePj pj = new ClientePj();
        pj.setTipoPessoa(TipoPessoa.JURIDICA);
        pj.setEmail(email);
        pj.setCnpj(cnpj);
        pj.setRazaoSocial(razaoSocial);
        pj.setDataCriacao(LocalDate.of(2010, 3, 15));
        return pj;
    }
}
