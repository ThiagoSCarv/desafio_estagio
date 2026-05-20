package com.thiago.desafio_estagio.shared.wicket.components.home;

import com.thiago.desafio_estagio.endereco.application.ViaCepClient;
import com.thiago.desafio_estagio.endereco.application.ViaCepResponseDto;
import com.thiago.desafio_estagio.shared.utils.JsUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;

public class EnderecoPanel extends Panel {

    @SpringBean
    private ViaCepClient viaCepClient;

    public static class EnderecoEntry implements Serializable {
        public String  cep               = "";
        public String  logradouro        = "";
        public Integer numero            = null;
        public String  bairro            = "";
        public String  cidade            = "";
        public String  estado            = "";
        public String  telefone          = "";
        public String  complemento       = "";
        public Boolean enderecoPrincipal = Boolean.FALSE;
    }

    public EnderecoPanel(String id, IModel<EnderecoEntry> model) {
        super(id, new CompoundPropertyModel<>(model));

        // Campos preenchidos via ViaCEP — ficam editáveis para correção manual
        TextField<String> logradouroField = new TextField<>("logradouro");
        logradouroField.setOutputMarkupId(true);

        TextField<String> bairroField = new TextField<>("bairro");
        bairroField.setOutputMarkupId(true);

        TextField<String> cidadeField = new TextField<>("cidade");
        cidadeField.setOutputMarkupId(true);

        TextField<String> estadoField = new TextField<>("estado");
        estadoField.setOutputMarkupId(true);

        TextField<String> cepField = new TextField<>("cep");
        cepField.add(AttributeModifier.replace("data-mask", "cep"));
        cepField.add(new AjaxFormComponentUpdatingBehavior("change") { // NOSONAR java:S110 — profundidade herdada do Wicket
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                EnderecoEntry entry = model.getObject();
                String cep = entry.cep != null ? entry.cep : "";
                if (cep.replaceAll("\\D", "").length() != 8) {
                    return;
                }
                try {
                    ViaCepResponseDto viaCep = viaCepClient.buscarPorCep(cep);
                    entry.logradouro = nvl(viaCep.logradouro());
                    entry.bairro     = nvl(viaCep.bairro());
                    entry.cidade     = nvl(viaCep.localidade());
                    entry.estado     = nvl(viaCep.uf());
                } catch (RuntimeException e) {
                    // CEP não encontrado — usuário preenche os campos manualmente
                }
                target.add(logradouroField, bairroField, cidadeField, estadoField);
            }

            @Override
            protected void onError(AjaxRequestTarget target, RuntimeException e) {
                // CEP ainda incompleto durante a digitação — ignorar
            }
        });

        add(cepField);
        add(logradouroField);
        add(new TextField<>("numero"));
        add(bairroField);
        add(cidadeField);
        add(estadoField);
        TextField<String> telefoneField = new TextField<>("telefone");
        telefoneField.add(AttributeModifier.replace("data-mask", "telefone"));
        add(telefoneField);
        add(new TextField<>("complemento"));
        add(new CheckBox("enderecoPrincipal"));

        add(new AjaxSubmitLink("remover") { // NOSONAR java:S110 — profundidade herdada do Wicket
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

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(JsUtils.MASKS));
    }

    protected boolean canRemove() {
        return true;
    }

    protected void onRemover(AjaxRequestTarget target) {
    }

    private static String nvl(String value) {
        return value != null ? value : "";
    }
}
