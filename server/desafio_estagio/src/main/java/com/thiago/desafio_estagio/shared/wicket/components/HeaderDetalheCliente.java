package com.thiago.desafio_estagio.shared.wicket.components;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjDto;
import com.thiago.desafio_estagio.cliente.application.ClienteService;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.shared.wicket.pages.home.HomePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class HeaderDetalheCliente extends Panel {

    @SpringBean
    private ClienteService clienteService;

    public HeaderDetalheCliente(String id, UUID clienteId) {
        super(id);

        IModel<ClienteDto> clienteModel = new LoadableDetachableModel<>() {
            @Override
            protected ClienteDto load() {
                return clienteService.buscarPorId(clienteId);
            }
        };


        add(new Link<Void>("voltar") {
            @Override
            public void onClick() {
                setResponsePage(HomePage.class);
            }
        });


        add(new Label("tipoPessoaLabel", () ->
                clienteModel.getObject().tipoPessoa() == TipoPessoa.FISICA
                        ? "Pessoa Física" : "Pessoa Jurídica"));


        add(new Label("nomeCliente", () -> {
            ClienteDto c = clienteModel.getObject();
            if (c instanceof ClientePfDto pf) return pf.nome();
            if (c instanceof ClientePjDto pj) return pj.razaoSocial();
            return "";
        }));

        WebMarkupContainer statusContainer = new WebMarkupContainer("statusContainer");
        statusContainer.add(AttributeModifier.replace("class",
                () -> "erp-status " + (clienteModel.getObject().ativo()
                        ? "erp-status--ativo" : "erp-status--inativo")));
        statusContainer.add(new Label("statusLabel",
                () -> clienteModel.getObject().ativo() ? "CLIENTE ATIVO" : "CLIENTE INATIVO"));
        statusContainer.add(new Label("desdeData",
                () -> clienteModel.getObject().criadoEm()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        add(statusContainer);

        RelatorioClienteModal modalRelatorio = new RelatorioClienteModal("modalRelatorio");
        add(modalRelatorio);

        add(new AjaxLink<Void>("relatorio") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                modalRelatorio.setCliente(clienteModel.getObject());
                target.add(modalRelatorio);
                target.appendJavaScript(
                    "bootstrap.Modal.getOrCreateInstance(document.getElementById('"
                        + modalRelatorio.getMarkupId() + "')).show();"
                );
            }
        });
    }
}
