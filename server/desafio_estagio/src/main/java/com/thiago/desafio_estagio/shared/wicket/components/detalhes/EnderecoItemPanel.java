package com.thiago.desafio_estagio.shared.wicket.components.detalhes;

import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
import com.thiago.desafio_estagio.shared.utils.DocumentFormat;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class EnderecoItemPanel extends Panel {

    public EnderecoItemPanel(String id, IModel<EnderecoDto> model) {
        super(id, model);
        setRenderBodyOnly(true);

        EnderecoDto e = model.getObject();

        WebMarkupContainer principalBadge = new WebMarkupContainer("principalBadge");
        principalBadge.setVisible(e.enderecoPrincipal());
        add(principalBadge);

        add(new AcoesPanelEndereco("acoes", model) {
            @Override
            protected void onEnderecoAtualizado(AjaxRequestTarget target) {
                EnderecoItemPanel.this.onEnderecoAtualizado(target);
            }

            @Override
            protected void onEnderecoExcluido(AjaxRequestTarget target) {
                EnderecoItemPanel.this.onEnderecoExcluido(target);
            }
        });

        String logradouroCompleto = e.logradouro() != null ? e.logradouro() : "—";
        if (e.numero() != null)
            logradouroCompleto += ", " + e.numero();
        add(new Label("logradouro", logradouroCompleto));

        add(new Label("bairro", e.bairro() != null ? e.bairro() : "—"));
        add(new Label("cep", DocumentFormat.formatCep(e.cep())));
        add(new Label("cidadeUf", formatCidadeUf(e.cidade(), e.estado())));
        add(new Label("telefone", e.telefone() != null ? DocumentFormat.formatTelefone(e.telefone()) : "—"));
        add(new Label("complemento", e.complemento() != null ? e.complemento() : "—"));
    }

    private static String formatCidadeUf(String cidade, String estado) {
        if (cidade != null && estado != null) return cidade + " · " + estado;
        if (cidade != null) return cidade;
        if (estado != null) return estado;
        return "—";
    }

    protected abstract void onEnderecoAtualizado(AjaxRequestTarget target);

    protected abstract void onEnderecoExcluido(AjaxRequestTarget target);
}
