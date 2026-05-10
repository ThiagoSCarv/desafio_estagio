package com.thiago.desafio_estagio;

import com.jayway.jsonpath.JsonPath;
import com.thiago.desafio_estagio.endereco.domain.EnderecoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Testes ponta-a-ponta do ciclo de vida de endereços.
// Cada método roda em sua própria transação revertida — sem necessidade de dados únicos globais.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:itdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EnderecoIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    void primeiroEndereco_semprePrincipal_ignoraFlagDto() throws Exception {
        // Envia enderecoPrincipal=false, mas por ser o primeiro endereço deve ser salvo como true
        String clienteId = criarClientePf("end1@it.com", "52998224725");

        mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadEndereco(false)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enderecoPrincipal").value(true));
    }

    @Test
    void segundoEnderecoPrincipal_desmarcaPrimeiro() throws Exception {
        String clienteId = criarClientePf("end2@it.com", "87748248800");

        // Primeiro endereço vira principal automaticamente
        String resp1 = mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadEndereco(false)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enderecoPrincipal").value(true))
                .andReturn().getResponse().getContentAsString();
        String endId1 = JsonPath.read(resp1, "$.id");

        // Segundo endereço cadastrado como principal → primeiro deve ser desmarcado
        mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadEndereco(true)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enderecoPrincipal").value(true));

        // desmarcarTodosPrincipaisDoCliente é @Modifying — bypassa o L1 cache; limpa antes de ler
        em.flush();
        em.clear();

        assertThat(enderecoRepository.findById(UUID.fromString(endId1))
                .orElseThrow().isEnderecoPrincipal()).isFalse();
    }

    @Test
    void patchTornarPrincipal_desmarcaAnterior() throws Exception {
        String clienteId = criarClientePf("end3@it.com", "12345678909");

        // Cria dois endereços; o primeiro vira principal, o segundo não
        String resp1 = mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadEndereco(false)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String endId1 = JsonPath.read(resp1, "$.id");

        String resp2 = mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadEndereco(false)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enderecoPrincipal").value(false))
                .andReturn().getResponse().getContentAsString();
        String endId2 = JsonPath.read(resp2, "$.id");

        // Promove o segundo a principal via PATCH
        mockMvc.perform(patch("/endereco/{id}", endId2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "enderecoPrincipal": true }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enderecoPrincipal").value(true));

        em.flush();
        em.clear();

        assertThat(enderecoRepository.findById(UUID.fromString(endId1))
                .orElseThrow().isEnderecoPrincipal()).isFalse();
        assertThat(enderecoRepository.findById(UUID.fromString(endId2))
                .orElseThrow().isEnderecoPrincipal()).isTrue();
    }

    @Test
    void deletarCliente_removeEnderecosPorCascata() throws Exception {
        String clienteId = criarClientePf("end4@it.com", "11144477735");

        mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadEndereco(true)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/clientes/{id}", clienteId))
                .andExpect(status().isNoContent());

        // @OnDelete(CASCADE) é DDL-level — Hibernate não rastreia; limpa L1 antes de consultar
        em.flush();
        em.clear();

        assertThat(enderecoRepository.findByClienteId(UUID.fromString(clienteId))).isEmpty();
    }

    @Test
    void deletarEnderecoPrincipal_deveRetornar409() throws Exception {
        String clienteId = criarClientePf("end5@it.com", "98765432100");

        String resp = mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadEndereco(true)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String endId = JsonPath.read(resp, "$.id");

        mockMvc.perform(delete("/endereco/{id}", endId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].field").value("enderecoPrincipal"));
    }

    @Test
    void deletarEnderecoNaoPrincipal_deveRetornar204() throws Exception {
        String clienteId = criarClientePf("end6@it.com", "23456789092");

        // Primeiro vira principal automaticamente
        mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadEndereco(false)))
                .andExpect(status().isCreated());

        // Segundo não é principal
        String resp2 = mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadEndereco(false)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enderecoPrincipal").value(false))
                .andReturn().getResponse().getContentAsString();
        String endId2 = JsonPath.read(resp2, "$.id");

        mockMvc.perform(delete("/endereco/{id}", endId2))
                .andExpect(status().isNoContent());
    }

    @Test
    void atualizarCamposEditaveis_deveRetornar200ComValoresAtualizados() throws Exception {
        String clienteId = criarClientePf("end7@it.com", "52998224725");

        String resp = mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadEndereco(true)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String endId = JsonPath.read(resp, "$.id");

        mockMvc.perform(patch("/endereco/{id}", endId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "numero": "999", "complemento": "Sala 5" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("999"))
                .andExpect(jsonPath("$.complemento").value("Sala 5"))
                .andExpect(jsonPath("$.logradouro").value("Rua das Flores"));
    }

    // --- helpers ---

    private String criarClientePf(String email, String cpf) throws Exception {
        String resposta = mockMvc.perform(post("/clientes/pf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "%s",
                                    "nome": "Cliente Teste",
                                    "cpf": "%s",
                                    "rg": "1234567",
                                    "dataNascimento": "1990-01-01"
                                }
                                """.formatted(email, cpf)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.id");
    }

    private String payloadEndereco(boolean principal) {
        return """
                {
                    "logradouro": "Rua das Flores",
                    "numero": "100",
                    "cep": "01310100",
                    "bairro": "Centro",
                    "cidade": "São Paulo",
                    "estado": "SP",
                    "enderecoPrincipal": %b
                }
                """.formatted(principal);
    }
}
