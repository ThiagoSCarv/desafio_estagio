package com.thiago.desafio_estagio.shared.wicket.components.detalhes;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjDto;
import com.thiago.desafio_estagio.cliente.application.ClienteService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class SectionDetalheCliente extends Panel {

    @SpringBean
    private ClienteService clienteService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public SectionDetalheCliente(String id, UUID clienteId) {
        super(id);

        IModel<ClienteDto> clienteModel = new LoadableDetachableModel<>() {
            @Override
            protected ClienteDto load() {
                return clienteService.buscarPorId(clienteId);
            }
        };

        // ── Seção PJ ──────────────────────────────────────────────────────────────
        WebMarkupContainer secaoPj = new WebMarkupContainer("secaoPj") {
            @Override
            public boolean isVisible() {
                return clienteModel.getObject() instanceof ClientePjDto;
            }
        };
        secaoPj.setOutputMarkupPlaceholderTag(true);

        secaoPj.add(new Label("razaoSocial", () ->
                clienteModel.getObject() instanceof ClientePjDto pj ? pj.razaoSocial() : "—"));
        secaoPj.add(new Label("emailPj", () -> clienteModel.getObject().email()));

        WebMarkupContainer statusPj = new WebMarkupContainer("statusPj");
        statusPj.add(AttributeModifier.replace("class", () ->
                "erp-status " + (clienteModel.getObject().ativo() ? "erp-status--ativo" : "erp-status--inativo")));
        statusPj.add(new Label("statusPjLabel", () ->
                clienteModel.getObject().ativo() ? "ATIVO" : "INATIVO"));
        secaoPj.add(statusPj);

        secaoPj.add(new Label("inscricaoEstadual", () -> {
            if (clienteModel.getObject() instanceof ClientePjDto pj)
                return pj.inscricaoEstadual() != null ? pj.inscricaoEstadual() : "—";
            return "—";
        }));
        secaoPj.add(new Label("cnpj", () ->
                clienteModel.getObject() instanceof ClientePjDto pj ? formatarCnpj(pj.cnpj()) : "—"));
        secaoPj.add(new Label("dataCriacao", () -> {
            if (clienteModel.getObject() instanceof ClientePjDto pj && pj.dataCriacao() != null)
                return pj.dataCriacao().format(DATE_FMT);
            return "—";
        }));

        add(secaoPj);

        // ── Seção PF ──────────────────────────────────────────────────────────────
        WebMarkupContainer secaoPf = new WebMarkupContainer("secaoPf") {
            @Override
            public boolean isVisible() {
                return clienteModel.getObject() instanceof ClientePfDto;
            }
        };
        secaoPf.setOutputMarkupPlaceholderTag(true);

        secaoPf.add(new Label("nome", () ->
                clienteModel.getObject() instanceof ClientePfDto pf ? pf.nome() : "—"));
        secaoPf.add(new Label("emailPf", () -> clienteModel.getObject().email()));

        WebMarkupContainer statusPf = new WebMarkupContainer("statusPf");
        statusPf.add(AttributeModifier.replace("class", () ->
                "erp-status " + (clienteModel.getObject().ativo() ? "erp-status--ativo" : "erp-status--inativo")));
        statusPf.add(new Label("statusPfLabel", () ->
                clienteModel.getObject().ativo() ? "ATIVO" : "INATIVO"));
        secaoPf.add(statusPf);

        secaoPf.add(new Label("cpf", () ->
                clienteModel.getObject() instanceof ClientePfDto pf ? formatarCpf(pf.cpf()) : "—"));
        secaoPf.add(new Label("rg", () -> {
            if (clienteModel.getObject() instanceof ClientePfDto pf)
                return pf.rg() != null ? pf.rg() : "—";
            return "—";
        }));
        secaoPf.add(new Label("dataNascimento", () -> {
            if (clienteModel.getObject() instanceof ClientePfDto pf && pf.dataNascimento() != null)
                return pf.dataNascimento().format(DATE_FMT);
            return "—";
        }));

        add(secaoPf);
    }

    private static String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf != null ? cpf : "—";
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    private static String formatarCnpj(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) return cnpj != null ? cnpj : "—";
        return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "."
                + cnpj.substring(5, 8) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12);
    }

}
