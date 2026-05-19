package com.thiago.desafio_estagio.shared.wicket.components.detalhes;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjDto;
import com.thiago.desafio_estagio.cliente.application.ClienteService;
import com.thiago.desafio_estagio.shared.utils.DocumentFormat;
import com.thiago.desafio_estagio.shared.wicket.components.home.EditarClienteModal;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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

    private final IModel<ClienteDto> clienteModel;
    private final WebMarkupContainer secaoPj;
    private final WebMarkupContainer secaoPf;

    public SectionDetalheCliente(String id, UUID clienteId) {
        super(id);

        clienteModel = new LoadableDetachableModel<>() {
            @Override
            protected ClienteDto load() {
                return clienteService.buscarPorId(clienteId);
            }
        };

        // Atualiza apenas as seções — não o painel inteiro — para o modal permanecer no DOM
        // enquanto o Bootstrap executa sua animação de fechamento e limpa o backdrop.
        EditarClienteModal editarModal = new EditarClienteModal("editarModal") {
            @Override
            protected void onAtualizado(AjaxRequestTarget target) {
                clienteModel.detach();
                target.add(secaoPj, secaoPf);
            }
        };
        add(editarModal);

        // ── Seção PJ ──────────────────────────────────────────────────────────────
        secaoPj = new WebMarkupContainer("secaoPj") {
            @Override
            public boolean isVisible() {
                return clienteModel.getObject() instanceof ClientePjDto;
            }
        };
        secaoPj.setOutputMarkupPlaceholderTag(true);

        secaoPj.add(new AjaxLink<Void>("editar") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                editarModal.setCliente(clienteModel.getObject());
                target.add(editarModal);
                target.appendJavaScript(
                    "bootstrap.Modal.getOrCreateInstance(document.getElementById('"
                    + editarModal.getMarkupId() + "')).show()");
            }
        });

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
                clienteModel.getObject() instanceof ClientePjDto pj ? DocumentFormat.formatCnpj(pj.cnpj()) : "—"));
        secaoPj.add(new Label("dataCriacao", () -> {
            if (clienteModel.getObject() instanceof ClientePjDto pj && pj.dataCriacao() != null)
                return pj.dataCriacao().format(DATE_FMT);
            return "—";
        }));

        add(secaoPj);

        // ── Seção PF ──────────────────────────────────────────────────────────────
        secaoPf = new WebMarkupContainer("secaoPf") {
            @Override
            public boolean isVisible() {
                return clienteModel.getObject() instanceof ClientePfDto;
            }
        };
        secaoPf.setOutputMarkupPlaceholderTag(true);

        secaoPf.add(new AjaxLink<Void>("editar") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                editarModal.setCliente(clienteModel.getObject());
                target.add(editarModal);
                target.appendJavaScript(
                    "bootstrap.Modal.getOrCreateInstance(document.getElementById('"
                    + editarModal.getMarkupId() + "')).show()");
            }
        });

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
                clienteModel.getObject() instanceof ClientePfDto pf ? DocumentFormat.formatCpf(pf.cpf()) : "—"));
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

}
