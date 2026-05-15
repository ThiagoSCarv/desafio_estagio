package com.thiago.desafio_estagio.shared.wicket.pages.home;

import com.thiago.desafio_estagio.shared.wicket.WicketApplication;
import com.thiago.desafio_estagio.shared.wicket.components.home.FooterPanel;
import com.thiago.desafio_estagio.shared.wicket.components.home.HeaderPanel;
import com.thiago.desafio_estagio.shared.wicket.components.home.ListaClientes;
import org.apache.wicket.ajax.AjaxRequestTarget;
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

        add(new FooterPanel("footer") {
            @Override
            protected void onAdicionou(AjaxRequestTarget target) {
                listaClientes.recarregarLista(target);
                target.add(header);
            }

            @Override
            protected void onImportou(AjaxRequestTarget target) {
                listaClientes.recarregarLista(target);
                target.add(header);
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
        response.render(CssHeaderItem.forReference(WicketApplication.TOKENS_CSS));
        response.render(CssHeaderItem.forReference(WicketApplication.GLOBAL_CSS));
        response.render(CssHeaderItem.forReference(WicketApplication.LAYOUT_CSS));
        response.render(CssHeaderItem.forReference(WicketApplication.TOOLBAR_CSS));
        response.render(CssHeaderItem.forReference(WicketApplication.LIST_CSS));
        response.render(CssHeaderItem.forReference(WicketApplication.MODAL_CSS));
        response.render(JavaScriptHeaderItem.forUrl(
            "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        ));
    }
}
