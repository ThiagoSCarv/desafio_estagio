package com.thiago.desafio_estagio;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Testes ponta-a-ponta do ciclo de vida de clientes PF e PJ.
// @Transactional garante rollback após cada método — sem necessidade de dados únicos por teste.
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
class ClienteIT {

    @Autowired
    private MockMvc mockMvc;

    private static final String EMAIL_PF = "joao@crud.it";
    private static final String CPF     = "52998224725";
    private static final String EMAIL_PJ = "empresa@crud.it";
    private static final String CNPJ    = "11222333000181";

    @Test
    void fluxoCompletoPf_criarBuscarAtualizarDeletar() throws Exception {
        // Criar
        String resposta = mockMvc.perform(post("/clientes/pf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadPf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(EMAIL_PF))
                .andExpect(jsonPath("$.tipoPessoa").value("FISICA"))
                .andExpect(jsonPath("$.ativo").value(true))
                .andReturn().getResponse().getContentAsString();

        String id = JsonPath.read(resposta, "$.id");

        // Buscar
        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL_PF))
                .andExpect(jsonPath("$.enderecos").isArray());

        // Atualizar
        mockMvc.perform(patch("/clientes/pf/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "nome": "João Atualizado", "ativo": false }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Atualizado"))
                .andExpect(jsonPath("$.ativo").value(false));

        // Deletar
        mockMvc.perform(delete("/clientes/{id}", id))
                .andExpect(status().isNoContent());

        // Verificar que foi deletado
        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].field").value("id"));
    }

    @Test
    void fluxoCompletoPj_criarBuscarAtualizar() throws Exception {
        // Criar
        String resposta = mockMvc.perform(post("/clientes/pj")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadPj("Empresa Original Ltda")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(EMAIL_PJ))
                .andExpect(jsonPath("$.tipoPessoa").value("JURIDICA"))
                .andExpect(jsonPath("$.cnpj").value(CNPJ))
                .andReturn().getResponse().getContentAsString();

        String id = JsonPath.read(resposta, "$.id");

        // Buscar
        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.razaoSocial").value("Empresa Original Ltda"));

        // Atualizar razão social e inscrição estadual
        mockMvc.perform(patch("/clientes/pj/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "razaoSocial": "Empresa Atualizada SA", "inscricaoEstadual": "IE-9999" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.razaoSocial").value("Empresa Atualizada SA"))
                .andExpect(jsonPath("$.inscricaoEstadual").value("IE-9999"));
    }

    @Test
    void listar_comFiltroTipoPessoa_retornaSoTipoCorreto() throws Exception {
        // Criar PF e PJ
        mockMvc.perform(post("/clientes/pf").contentType(MediaType.APPLICATION_JSON).content(payloadPf()))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/clientes/pj").contentType(MediaType.APPLICATION_JSON).content(payloadPj("Empresa SA")))
                .andExpect(status().isCreated());

        // Listar somente PF
        mockMvc.perform(get("/clientes").param("tipoPessoa", "FISICA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].tipoPessoa").value("FISICA"));

        // Listar somente PJ
        mockMvc.perform(get("/clientes").param("tipoPessoa", "JURIDICA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].tipoPessoa").value("JURIDICA"));
    }

    @Test
    void emailJaCadastradoEmPf_deveImpedirCadastroPj() throws Exception {
        // Criar PF com EMAIL_PF
        mockMvc.perform(post("/clientes/pf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadPf()))
                .andExpect(status().isCreated());

        // Tentar criar PJ com o mesmo e-mail → 409
        mockMvc.perform(post("/clientes/pj")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "%s",
                                    "cnpj": "%s",
                                    "razaoSocial": "Empresa SA",
                                    "dataCriacao": "2010-03-15"
                                }
                                """.formatted(EMAIL_PF, CNPJ)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }

    @Test
    void cpfDuplicado_deveRetornar409() throws Exception {
        mockMvc.perform(post("/clientes/pf").contentType(MediaType.APPLICATION_JSON).content(payloadPf()))
                .andExpect(status().isCreated());

        // Segundo cadastro com mesmo CPF mas e-mail diferente → 409
        mockMvc.perform(post("/clientes/pf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "outro@it.com",
                                    "nome": "Outro",
                                    "cpf": "%s",
                                    "rg": "7654321",
                                    "dataNascimento": "1995-06-15"
                                }
                                """.formatted(CPF)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].field").value("cpf"));
    }

    // --- helpers ---

    private String payloadPf() {
        return """
                {
                    "email": "%s",
                    "nome": "João Silva",
                    "cpf": "%s",
                    "rg": "1234567",
                    "dataNascimento": "1990-01-01"
                }
                """.formatted(EMAIL_PF, CPF);
    }

    private String payloadPj(String razaoSocial) {
        return """
                {
                    "email": "%s",
                    "cnpj": "%s",
                    "razaoSocial": "%s",
                    "dataCriacao": "2010-03-15"
                }
                """.formatted(EMAIL_PJ, CNPJ, razaoSocial);
    }
}
