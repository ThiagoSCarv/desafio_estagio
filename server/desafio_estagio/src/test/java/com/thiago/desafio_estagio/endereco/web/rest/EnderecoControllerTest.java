package com.thiago.desafio_estagio.endereco.web.rest;

import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
import com.thiago.desafio_estagio.endereco.application.EnderecoService;
import com.thiago.desafio_estagio.endereco.domain.exceptions.EnderecoNaoEncontradoException;
import com.thiago.desafio_estagio.endereco.domain.exceptions.EnderecoPrincipalException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnderecoController.class)
class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnderecoService enderecoService;

    // --- POST /endereco/{clienteId} ---

    @Test
    void criar_payloadValido_deveRetornar201ComDto() throws Exception {
        UUID clienteId = UUID.randomUUID();
        when(enderecoService.criar(eq(clienteId), any())).thenReturn(enderecoDto());

        mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadCriarValido()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.logradouro").value("Rua A"))
                .andExpect(jsonPath("$.cep").value("01310100"))
                .andExpect(jsonPath("$.enderecoPrincipal").value(true));
    }

    @Test
    void criar_cepInvalido_deveRetornar422ComCampoCep() throws Exception {
        UUID clienteId = UUID.randomUUID();

        mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "logradouro": "Rua A",
                                    "numero": "100",
                                    "cep": "0131",
                                    "bairro": "Centro",
                                    "cidade": "São Paulo",
                                    "estado": "SP",
                                    "enderecoPrincipal": true
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[*].field", hasItem("cep")));
    }

    @Test
    void criar_estadoComTamanhoInvalido_deveRetornar422ComCampoEstado() throws Exception {
        UUID clienteId = UUID.randomUUID();

        mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "logradouro": "Rua A",
                                    "numero": "100",
                                    "cep": "01310100",
                                    "bairro": "Centro",
                                    "cidade": "São Paulo",
                                    "estado": "SPX",
                                    "enderecoPrincipal": true
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[*].field", hasItem("estado")));
    }

    @Test
    void criar_clienteNaoEncontrado_deveRetornar404() throws Exception {
        UUID clienteId = UUID.randomUUID();
        when(enderecoService.criar(eq(clienteId), any())).thenThrow(new ClienteNaoEncontradoException());

        mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadCriarValido()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].field").value("id"));
    }

    @Test
    void criar_camposObrigatoriosAusentes_deveRetornar422() throws Exception {
        UUID clienteId = UUID.randomUUID();

        mockMvc.perform(post("/endereco/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors").isArray());
    }

    // --- PATCH /endereco/{id} ---

    @Test
    void atualizar_payloadValido_deveRetornar200ComDto() throws Exception {
        UUID id = UUID.randomUUID();
        when(enderecoService.atualizar(eq(id), any())).thenReturn(enderecoDto());

        mockMvc.perform(patch("/endereco/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "numero": "200", "complemento": "Apto 5" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua A"));
    }

    @Test
    void atualizar_enderecoNaoEncontrado_deveRetornar404() throws Exception {
        UUID id = UUID.randomUUID();
        when(enderecoService.atualizar(eq(id), any())).thenThrow(new EnderecoNaoEncontradoException());

        mockMvc.perform(patch("/endereco/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "numero": "200" }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].field").value("id"));
    }

    // --- DELETE /endereco/{id} ---

    @Test
    void deletar_enderecoNaoPrincipal_deveRetornar204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/endereco/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletar_enderecoNaoEncontrado_deveRetornar404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new EnderecoNaoEncontradoException()).when(enderecoService).deletar(id);

        mockMvc.perform(delete("/endereco/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].field").value("id"));
    }

    @Test
    void deletar_enderecoPrincipal_deveRetornar409ComCampoEnderecoPrincipal() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new EnderecoPrincipalException()).when(enderecoService).deletar(id);

        mockMvc.perform(delete("/endereco/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].field").value("enderecoPrincipal"));
    }

    // --- helpers ---

    private String payloadCriarValido() {
        return """
                {
                    "logradouro": "Rua A",
                    "numero": "100",
                    "cep": "01310100",
                    "bairro": "Centro",
                    "cidade": "São Paulo",
                    "estado": "SP",
                    "enderecoPrincipal": true
                }
                """;
    }

    private EnderecoDto enderecoDto() {
        return new EnderecoDto(
                UUID.randomUUID(),
                "Rua A", "100", "01310100", "Centro",
                null, "São Paulo", "SP", true, null
        );
    }
}
