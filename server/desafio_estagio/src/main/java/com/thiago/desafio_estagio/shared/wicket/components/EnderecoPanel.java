package com.thiago.desafio_estagio.shared.wicket.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public class EnderecoPanel extends Panel {

    public static class EnderecoEntry implements Serializable {
        public String  logradouro        = "";
        public String  numero            = "";
        public String  cep               = "";
        public String  bairro            = "";
        public String  cidade            = "";
        public String  estado            = "";
        public String  telefone          = "";
        public String  complemento       = "";
        public Boolean enderecoPrincipal = Boolean.FALSE;
    }

    public EnderecoPanel(String id, IModel<EnderecoEntry> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new TextField<>("logradouro"));
        add(new TextField<>("numero"));
        add(new TextField<>("cep"));
        add(new TextField<>("bairro"));
        add(new TextField<>("cidade"));
        add(new TextField<>("estado"));
        add(new TextField<>("telefone"));
        add(new TextField<>("complemento"));
        add(new CheckBox("enderecoPrincipal"));
        add(new AjaxSubmitLink("remover") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                onRemover(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                onRemover(target);
            }

            @Override
            public boolean isVisible() {
                return canRemove();
            }
        });
    }

    protected boolean canRemove() {
        return true;
    }

    protected void onRemover(AjaxRequestTarget target) {
    }
}
