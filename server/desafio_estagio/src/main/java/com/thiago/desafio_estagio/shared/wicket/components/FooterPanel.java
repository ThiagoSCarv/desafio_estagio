package com.thiago.desafio_estagio.shared.wicket.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

public class FooterPanel extends Panel {

    public FooterPanel(String id) {
        super(id);

        AdicionarClienteModal modalAdicionar = new AdicionarClienteModal("modalAdicionar") {
            @Override
            protected void onAdicionado(AjaxRequestTarget target) {
                onAdicionou(target);
            }
        };
        add(modalAdicionar);

        add(new AjaxLink<Void>("adicionarCliente") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.appendJavaScript(
                    "bootstrap.Modal.getOrCreateInstance(document.getElementById('" + modalAdicionar.getMarkupId() + "')).show();"
                );
            }
        });

        ImportarClientesModal modalImportar = new ImportarClientesModal("modalImportar") {
            @Override
            protected void onImportou(AjaxRequestTarget target) {
                onImportou(target);
            }
        };
        add(modalImportar);

        add(new AjaxLink<Void>("importarClientes") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.appendJavaScript(
                    "bootstrap.Modal.getOrCreateInstance(document.getElementById('" + modalImportar.getMarkupId() + "')).show();"
                );
            }
        });
    }

    protected void onAdicionou(AjaxRequestTarget target) {}

    protected void onImportou(AjaxRequestTarget target) {}
}
