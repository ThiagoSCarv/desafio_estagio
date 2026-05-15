package com.thiago.desafio_estagio.shared.wicket.pages.detalhes;

import com.thiago.desafio_estagio.shared.wicket.WicketApplication;
import com.thiago.desafio_estagio.shared.wicket.components.HeaderDetalheCliente;
import com.thiago.desafio_estagio.shared.wicket.components.ListaEnderecosCliente;
import com.thiago.desafio_estagio.shared.wicket.components.SectionDetalheCliente;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.UUID;

public class DetalhesClientePage extends WebPage {

    public DetalhesClientePage(PageParameters params) {
        super(params);

        UUID clienteId = UUID.fromString(params.get("id").toString());

        add(new HeaderDetalheCliente("header", clienteId));
        add(new SectionDetalheCliente("secaoDetalhe", clienteId));
        add(new ListaEnderecosCliente("listaEnderecos", clienteId));
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
