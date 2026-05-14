package com.thiago.desafio_estagio.shared.wicket.pages.home;

import com.thiago.desafio_estagio.shared.wicket.WicketApplication;
import com.thiago.desafio_estagio.shared.wicket.components.AdicionarClienteModal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;

public class HomePage extends WebPage {

    public HomePage() {
        HeaderPanel header = new HeaderPanel("header");
        add(header);

        ListaClientes listaClientes = new ListaClientes("listaClientes") {
            @Override
            protected void onAtualizou(AjaxRequestTarget target) {
                target.add(header);
            }
        };
        add(listaClientes);

        AdicionarClienteModal modalAdicionar = new AdicionarClienteModal("modalAdicionar") {
            @Override
            protected void onAdicionado(AjaxRequestTarget target) {
                listaClientes.recarregarLista(target);
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
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forUrl(
            "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        ));
        response.render(CssHeaderItem.forReference(WicketApplication.THEME_CSS));
        response.render(JavaScriptHeaderItem.forUrl(
            "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        ));
    }
}
