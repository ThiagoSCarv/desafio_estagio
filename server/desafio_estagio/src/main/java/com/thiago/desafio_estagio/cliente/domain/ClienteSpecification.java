package com.thiago.desafio_estagio.repository;

import com.thiago.desafio_estagio.models.Cliente;
import com.thiago.desafio_estagio.models.ClientePf;
import com.thiago.desafio_estagio.models.ClientePj;
import com.thiago.desafio_estagio.models.TipoPessoa;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

//Cria Specifications para consultas dinâmicas de Cliente, cada filtro é opcional
public class ClienteSpecification {

    public static Specification<Cliente> comFiltros(TipoPessoa tipoPessoa, String documento, String nome) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (tipoPessoa != null) {
                predicates.add(cb.equal(root.get("tipoPessoa"), tipoPessoa));
            }

            if (documento != null && !documento.isBlank()) {
                /*cb.treat() permite acessar campos de ClientePf (cpf) e ClientePj (cnpj) numa unica query.
                O OR garante que a busca funciona independente do tipo do cliente.*/ 
                Predicate porCpf = cb.like(
                        cb.treat(root, ClientePf.class).<String>get("cpf"),
                        "%" + documento + "%"
                );
                Predicate porCnpj = cb.like(
                        cb.treat(root, ClientePj.class).<String>get("cnpj"),
                        "%" + documento + "%"
                );
                predicates.add(cb.or(porCpf, porCnpj));
            }

            if (nome != null && !nome.isBlank()) {
                String pattern = "%" + nome.toLowerCase() + "%";
                Predicate porNome = cb.like(
                        cb.lower(cb.treat(root, ClientePf.class).<String>get("nome")), pattern
                );
                Predicate porRazaoSocial = cb.like(
                        cb.lower(cb.treat(root, ClientePj.class).<String>get("razaoSocial")), pattern
                );
                predicates.add(cb.or(porNome, porRazaoSocial));
            }

            // AND entre todos os filtros, caso não tenha filtros retorna todos os registros.
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
