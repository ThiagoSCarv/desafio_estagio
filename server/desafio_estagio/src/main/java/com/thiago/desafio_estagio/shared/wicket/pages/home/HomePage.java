package com.thiago.desafio_estagio.shared.wicket.pages.home;

import com.thiago.desafio_estagio.shared.wicket.WicketApplication;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssUrlReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptUrlReferenceHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.PackageResourceReference;

public class HomePage extends WebPage {

    public HomePage() {
        add(new HeaderPanel("header"));
        add(new ListaClientes("listaClientes"));
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
