package com.thiago.desafio_estagio.cliente.web.rest;

import com.thiago.desafio_estagio.cliente.application.ClientePfDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfService;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.CpfJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.EmailJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.RgJaCadastradoException;
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

@WebMvcTest(ClientePfController.class)
class ClientePfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientePfService clientePfService;

    // --- POST /clientes/pf ---

    @Test
    void criar_payloadValido_deveRetornar201ComDto() throws Exception {
        ClientePfDto dto = clientePfDto();
        when(clientePfService.criar(any())).thenReturn(dto);

        mockMvc.perform(post("/clientes/pf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadValido()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.tipoPessoa").value("FISICA"));
    }

    @Test
    void criar_cpfInvalido_deveRetornar422ComCampoCpf() throws Exception {
        // CPF com todos dígitos iguais → @ValidCpf rejeita
        mockMvc.perform(post("/clientes/pf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "joao@email.com",
                                    "nome": "João Silva",
                                    "cpf": "00000000000",
                                    "rg": "1234567",
                                    "dataNascimento": "1990-01-01"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[*].field", hasItem("cpf")));
    }

    @Test
    void criar_emailMalformado_deveRetornar422ComCampoEmail() throws Exception {
        mockMvc.perform(post("/clientes/pf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "nao-e-email",
                                    "nome": "João Silva",
                                    "cpf": "52998224725",
                                    "rg": "1234567",
                                    "dataNascimento": "1990-01-01"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[*].field", hasItem("email")));
    }

    @Test
    void criar_camposObrigatoriosAusentes_deveRetornar422() throws Exception {
        // body vazio — todos os @NotBlank e @NotNull falham
        mockMvc.perform(post("/clientes/pf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void criar_emailDuplicado_deveRetornar409ComCampoEmail() throws Exception {
        when(clientePfService.criar(any())).thenThrow(new EmailJaCadastradoException());

        mockMvc.perform(post("/clientes/pf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadValido()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("Email já cadastrado"));
    }

    @Test
    void criar_cpfDuplicado_deveRetornar409ComCampoCpf() throws Exception {
        when(clientePfService.criar(any())).thenThrow(new CpfJaCadastradoException());

        mockMvc.perform(post("/clientes/pf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadValido()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].field").value("cpf"))
                .andExpect(jsonPath("$.errors[0].message").value("CPF já cadastrado"));
    }

    @Test
    void criar_rgDuplicado_deveRetornar409ComCampoRg() throws Exception {
        when(clientePfService.criar(any())).thenThrow(new RgJaCadastradoException());

        mockMvc.perform(post("/clientes/pf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadValido()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].field").value("rg"));
    }

    // --- PATCH /clientes/pf/{id} ---

    @Test
    void atualizar_payloadValido_deveRetornar200ComDto() throws Exception {
        UUID id = UUID.randomUUID();
        when(clientePfService.atualizar(eq(id), any())).thenReturn(clientePfDto());

        mockMvc.perform(patch("/clientes/pf/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "nome": "João Atualizado" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void atualizar_clienteNaoEncontrado_deveRetornar404() throws Exception {
        UUID id = UUID.randomUUID();
        when(clientePfService.atualizar(eq(id), any())).thenThrow(new ClienteNaoEncontradoException());

        mockMvc.perform(patch("/clientes/pf/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "nome": "João" }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].field").value("id"));
    }

    // --- helpers ---

    private String payloadValido() {
        return """
                {
                    "email": "joao@email.com",
                    "nome": "João Silva",
                    "cpf": "52998224725",
                    "rg": "1234567",
                    "dataNascimento": "1990-01-01"
                }
                """;
    }

    private ClientePfDto clientePfDto() {
        return new ClientePfDto(
                UUID.randomUUID(), TipoPessoa.FISICA, "joao@email.com", true,
                null, null,
                "João Silva", "52998224725", "1234567", LocalDate.of(1990, 1, 1),
                List.of()
        );
    }
}
