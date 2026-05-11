package com.thiago.desafio_estagio.shared.wicket.pages.home;

import com.thiago.desafio_estagio.cliente.application.ClienteService;
import com.thiago.desafio_estagio.shared.wicket.WicketApplication;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssUrlReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptUrlReferenceHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class HomePage extends WebPage {

    @SpringBean
    private ClienteService clienteService;

    public HomePage() {
        long total = clienteService.contarTotal();
        long ativos = clienteService.contarAtivos();

        add(new Label("totalRegistros", String.format("%03d", total)));
        add(new Label("totalAtivos", String.format("%03d", ativos)));
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
