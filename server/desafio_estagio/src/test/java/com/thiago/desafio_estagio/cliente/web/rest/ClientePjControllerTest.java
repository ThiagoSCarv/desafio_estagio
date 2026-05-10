package com.thiago.desafio_estagio.cliente.web.rest;

import com.thiago.desafio_estagio.cliente.application.ClientePjDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjService;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.CnpjJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.EmailJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.RazaoSocialJaCadastradaException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientePjController.class)
class ClientePjControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientePjService clientePjService;

    // --- POST /clientes/pj ---

    @Test
    void criar_payloadValido_deveRetornar201ComDto() throws Exception {
        when(clientePjService.criar(any())).thenReturn(clientePjDto());

        mockMvc.perform(post("/clientes/pj")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadValido()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("empresa@email.com"))
                .andExpect(jsonPath("$.cnpj").value("11222333000181"))
                .andExpect(jsonPath("$.tipoPessoa").value("JURIDICA"));
    }

    @Test
    void criar_cnpjInvalido_deveRetornar422ComCampoCnpj() throws Exception {
        // CNPJ com todos dígitos iguais → @ValidCnpj rejeita
        mockMvc.perform(post("/clientes/pj")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "empresa@email.com",
                                    "cnpj": "11111111111111",
                                    "razaoSocial": "Empresa SA",
                                    "dataCriacao": "2010-03-15"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[*].field", hasItem("cnpj")));
    }

    @Test
    void criar_camposObrigatoriosAusentes_deveRetornar422() throws Exception {
        mockMvc.perform(post("/clientes/pj")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void criar_emailDuplicado_deveRetornar409ComCampoEmail() throws Exception {
        when(clientePjService.criar(any())).thenThrow(new EmailJaCadastradoException());

        mockMvc.perform(post("/clientes/pj")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadValido()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }

    @Test
    void criar_cnpjDuplicado_deveRetornar409ComCampoCnpj() throws Exception {
        when(clientePjService.criar(any())).thenThrow(new CnpjJaCadastradoException());

        mockMvc.perform(post("/clientes/pj")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadValido()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].field").value("cnpj"));
    }

    @Test
    void criar_razaoSocialDuplicada_deveRetornar409ComCampoRazaoSocial() throws Exception {
        when(clientePjService.criar(any())).thenThrow(new RazaoSocialJaCadastradaException());

        mockMvc.perform(post("/clientes/pj")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadValido()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].field").value("razaoSocial"));
    }

    // --- PATCH /clientes/pj/{id} ---

    @Test
    void atualizar_payloadValido_deveRetornar200ComDto() throws Exception {
        UUID id = UUID.randomUUID();
        when(clientePjService.atualizar(eq(id), any())).thenReturn(clientePjDto());

        mockMvc.perform(patch("/clientes/pj/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "razaoSocial": "Empresa Atualizada SA" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cnpj").value("11222333000181"));
    }

    @Test
    void atualizar_clienteNaoEncontrado_deveRetornar404() throws Exception {
        UUID id = UUID.randomUUID();
        when(clientePjService.atualizar(eq(id), any())).thenThrow(new ClienteNaoEncontradoException());

        mockMvc.perform(patch("/clientes/pj/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "razaoSocial": "Nova Empresa" }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].field").value("id"));
    }

    // --- helpers ---

    private String payloadValido() {
        return """
                {
                    "email": "empresa@email.com",
                    "cnpj": "11222333000181",
                    "razaoSocial": "Empresa SA",
                    "dataCriacao": "2010-03-15"
                }
                """;
    }

    private ClientePjDto clientePjDto() {
        return new ClientePjDto(
                UUID.randomUUID(), TipoPessoa.JURIDICA, "empresa@email.com", true,
                null, null,
                "11222333000181", "Empresa SA", null, LocalDate.of(2010, 3, 15),
                List.of()
        );
    }
}
