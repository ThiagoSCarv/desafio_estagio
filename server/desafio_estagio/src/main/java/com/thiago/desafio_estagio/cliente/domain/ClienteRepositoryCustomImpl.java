package com.thiago.desafio_estagio.cliente.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ClienteRepositoryCustomImpl implements ClienteRepositoryCustom {

    private final EntityManager em;

    /*
     * TYPE(c) antes de cada TREAT garante que o Hibernate gera INNER JOIN apenas no
     * subtipo correto. Sem esse guard, um OR entre TREAT(PF) e TREAT(PJ) na mesma
     * cláusula WHERE pode forçar joins nos dois subtipos simultaneamente, retornando
     * resultados incorretos na herança JOINED.
     */
    private static final String WHERE_FILTROS = """
            FROM Cliente c
            WHERE (:tipoPessoa IS NULL OR c.tipoPessoa = :tipoPessoa)
              AND (:doc IS NULL
                   OR (TYPE(c) = ClientePf AND TREAT(c AS ClientePf).cpf        LIKE :docPattern)
                   OR (TYPE(c) = ClientePj AND TREAT(c AS ClientePj).cnpj       LIKE :docPattern))
              AND (:nome IS NULL
                   OR (TYPE(c) = ClientePf AND LOWER(TREAT(c AS ClientePf).nome)            LIKE :nomePattern)
                   OR (TYPE(c) = ClientePj AND LOWER(TREAT(c AS ClientePj).razaoSocial)     LIKE :nomePattern))
            """;

    @Override
    public Page<Cliente> buscarComFiltros(TipoPessoa tipoPessoa, String documento, String nome, Pageable pageable) {
        String docNorm = normalizarDoc(documento);
        String nomeNorm = normalizarNome(nome);

        TypedQuery<Cliente> dataQuery = em.createQuery("SELECT c " + WHERE_FILTROS, Cliente.class);
        TypedQuery<Long> countQuery = em.createQuery("SELECT COUNT(c) " + WHERE_FILTROS, Long.class);

        aplicarParametros(dataQuery, tipoPessoa, docNorm, nomeNorm);
        aplicarParametros(countQuery, tipoPessoa, docNorm, nomeNorm);

        long total = countQuery.getSingleResult();
        List<Cliente> content = Objects.requireNonNullElse(
                dataQuery
                        .setFirstResult((int) pageable.getOffset())
                        .setMaxResults(pageable.getPageSize())
                        .getResultList(),
                List.of());

        return new PageImpl<>(content, pageable, total);
    }

    private void aplicarParametros(Query q, TipoPessoa tipoPessoa, String doc, String nome) {
        q.setParameter("tipoPessoa", tipoPessoa);
        q.setParameter("doc", doc);
        q.setParameter("docPattern", doc != null ? "%" + doc + "%" : null);
        q.setParameter("nome", nome);
        q.setParameter("nomePattern", nome != null ? "%" + nome + "%" : null);
    }

    // Documentos sao persistidos so com digitos; normaliza para aceitar mascaras na busca.
    private String normalizarDoc(String documento) {
        if (documento == null) return null;
        String digits = documento.replaceAll("\\D", "");
        return digits.isEmpty() ? null : digits;
    }

    private String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) return null;
        return nome.toLowerCase();
    }
}
