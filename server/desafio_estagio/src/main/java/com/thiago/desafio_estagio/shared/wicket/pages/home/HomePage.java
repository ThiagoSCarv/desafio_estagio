package com.thiago.desafio_estagio.shared.wicket.pages.home;

import com.thiago.desafio_estagio.shared.wicket.WicketApplication;
import com.thiago.desafio_estagio.shared.wicket.components.AdicionarClienteModal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssUrlReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptUrlReferenceHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.PackageResourceReference;

public class HomePage extends WebPage {

    private static final String MODAL_CLEANUP_JS =
        "document.querySelectorAll('.modal-backdrop').forEach(function(b){b.remove();});" +
        "document.body.classList.remove('modal-open');" +
        "document.body.style.removeProperty('overflow');" +
        "document.body.style.removeProperty('padding-right');";

    public HomePage() {
        add(new HeaderPanel("header"));

        ListaClientes listaClientes = new ListaClientes("listaClientes");
        add(listaClientes);

        AdicionarClienteModal modalAdicionar = new AdicionarClienteModal("modalAdicionar") {
            @Override
            protected void onAdicionado(AjaxRequestTarget target) {
                target.appendJavaScript(MODAL_CLEANUP_JS);
                target.add(listaClientes);
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
        response.render(CssUrlReferenceHeaderItem.forUrl(
            "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        ));
        response.render(CssHeaderItem.forReference(
            new PackageResourceReference(WicketApplication.class, "theme.css")
        ));
        response.render(JavaScriptUrlReferenceHeaderItem.forUrl(
            "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        ));
    }
}
