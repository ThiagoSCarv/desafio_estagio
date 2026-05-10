package com.thiago.desafio_estagio.cliente.web.rest;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfDto;
import com.thiago.desafio_estagio.cliente.application.ClienteService;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Test
    void listar_deveRetornar200ComPaginaDeClientes() throws Exception {
        ClientePfDto pfDto = clientePfDto(UUID.randomUUID());
        when(clienteService.listarTodos(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(pfDto)));

        mockMvc.perform(get("/clientes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].email").value("joao@email.com"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void listar_comFiltros_deveRetornar200() throws Exception {
        when(clienteService.listarTodos(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/clientes")
                        .param("tipoPessoa", "FISICA")
                        .param("nome", "João")
                        .param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void buscar_clienteExistente_deveRetornar200ComDto() throws Exception {
        UUID id = UUID.randomUUID();
        ClientePfDto pfDto = clientePfDto(id);
        when(clienteService.buscarPorId(id)).thenReturn(pfDto);

        mockMvc.perform(get("/clientes/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.enderecos").isArray());
    }

    @Test
    void buscar_clienteNaoEncontrado_deveRetornar404() throws Exception {
        UUID id = UUID.randomUUID();
        when(clienteService.buscarPorId(id)).thenThrow(new ClienteNaoEncontradoException());

        mockMvc.perform(get("/clientes/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].field").value("id"));
    }

    @Test
    void deletar_clienteExistente_deveRetornar204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/clientes/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletar_clienteNaoEncontrado_deveRetornar404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ClienteNaoEncontradoException()).when(clienteService).deletar(id);

        mockMvc.perform(delete("/clientes/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].field").value("id"));
    }

    private ClientePfDto clientePfDto(UUID id) {
        return new ClientePfDto(
                id, TipoPessoa.FISICA, "joao@email.com", true,
                null, null,
                "João Silva", "52998224725", "1234567", LocalDate.of(1990, 1, 1),
                List.of()
        );
    }
}
