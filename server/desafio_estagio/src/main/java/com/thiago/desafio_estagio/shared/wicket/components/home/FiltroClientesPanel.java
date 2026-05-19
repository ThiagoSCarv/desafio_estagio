package com.thiago.desafio_estagio.shared.wicket.components.home;

import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.shared.utils.JsUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class FiltroClientesPanel extends Panel {

    private final Model<TipoPessoa> tipoFiltroModel;

    private AjaxLink<Void> linkTodos;
    private AjaxLink<Void> linkFisica;
    private AjaxLink<Void> linkJuridica;

    public FiltroClientesPanel(String id, Model<String> nomeFilter, Model<String> documentoFilter, Model<TipoPessoa> tipoFiltroModel) {
        super(id);
        this.tipoFiltroModel = tipoFiltroModel;

        Form<Void> filtroForm = new Form<>("filtroForm");
        filtroForm.add(AttributeModifier.replace("onsubmit", "return false;"));
        add(filtroForm);

        linkTodos = tipoLink("filtroTodos", null);
        linkFisica = tipoLink("filtroFisica", TipoPessoa.FISICA);
        linkJuridica = tipoLink("filtroJuridica", TipoPessoa.JURIDICA);

        linkTodos.setOutputMarkupId(true);
        linkFisica.setOutputMarkupId(true);
        linkJuridica.setOutputMarkupId(true);

        filtroForm.add(linkTodos, linkFisica, linkJuridica);

        TextField<String> nomeField = new TextField<>("filtroNome", nomeFilter);
        nomeField.add(atualizarAoMudar());
        filtroForm.add(nomeField);

        TextField<String> documentoField = new TextField<>("filtroDocumento", documentoFilter);
        documentoField.add(atualizarAoMudar());
        filtroForm.add(documentoField);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(JsUtils.MASKS));
    }

    protected void onFiltroMudou(AjaxRequestTarget target) {}

    private AjaxFormComponentUpdatingBehavior atualizarAoMudar() {
        return new AjaxFormComponentUpdatingBehavior("input") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                onFiltroMudou(target);
            }
        };
    }

    private AjaxLink<Void> tipoLink(String id, TipoPessoa tipo) {
        AjaxLink<Void> link = new AjaxLink<>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                tipoFiltroModel.setObject(tipo);
                target.add(linkTodos, linkFisica, linkJuridica);
                onFiltroMudou(target);
            }
        };
        link.add(new AttributeModifier("class", new IModel<String>() {
            @Override
            public String getObject() {
                return "erp-filter-tab" + (tipoFiltroModel.getObject() == tipo ? " erp-filter-tab--ativo" : "");
            }
        }));
        link.add(new AttributeModifier("aria-selected", new IModel<String>() {
            @Override
            public String getObject() {
                return String.valueOf(tipoFiltroModel.getObject() == tipo);
            }
        }));
        return link;
    }
}
