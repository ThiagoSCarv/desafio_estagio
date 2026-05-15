package com.thiago.desafio_estagio.shared.wicket.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.UUID;

public class FooterDetalheCliente extends Panel {

    public FooterDetalheCliente(String id, UUID clienteId, ListaEnderecosCliente listaEnderecos) {
        super(id);

        AdicionarEnderecoModal modal = new AdicionarEnderecoModal("modalAdicionarEndereco", clienteId) {
            @Override
            protected void onAdicionado(AjaxRequestTarget target) {
                listaEnderecos.recarregarLista(target);
            }
        };
        add(modal);

        add(new AjaxLink<Void>("btnAdicionarEndereco") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.appendJavaScript(
                    "bootstrap.Modal.getOrCreateInstance(document.getElementById('" + modal.getMarkupId() + "')).show();"
                );
            }
        });
    }
}
