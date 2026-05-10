package com.thiago.desafio_estagio.cliente.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteRepositoryCustom {

    Page<Cliente> buscarComFiltros(TipoPessoa tipoPessoa, String documento, String nome, Pageable pageable);
}
