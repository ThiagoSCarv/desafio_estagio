package com.thiago.desafio_estagio.shared.wicket.components;

import com.thiago.desafio_estagio.endereco.application.EnderecoCreateDto;
import com.thiago.desafio_estagio.endereco.application.EnderecoService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.UUID;

public class AdicionarEnderecoModal extends Panel {

    @SpringBean
    private EnderecoService enderecoService;

    private static class EnderecoData implements Serializable {
        String  logradouro        = "";
        String  numero            = "";
        String  cep               = "";
        String  bairro            = "";
        String  telefone          = "";
        String  cidade            = "";
        String  estado            = "";
        Boolean enderecoPrincipal = Boolean.FALSE;
        String  complemento       = "";
    }

    private final EnderecoData formData = new EnderecoData();
    private final FeedbackPanel feedback;

    public AdicionarEnderecoModal(String id, UUID clienteId) {
        super(id);
        setOutputMarkupId(true);

        Form<EnderecoData> form = new Form<>("form", new CompoundPropertyModel<>(formData));
        add(form);

        feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        form.add(new TextField<>("logradouro"));
        form.add(new TextField<>("numero"));
        form.add(new TextField<>("cep"));
        form.add(new TextField<>("bairro"));
        form.add(new TextField<>("telefone"));
        form.add(new TextField<>("cidade"));
        form.add(new TextField<>("estado"));
        form.add(new CheckBox("enderecoPrincipal"));
        form.add(new TextField<>("complemento"));

        form.add(new AjaxButton("salvar", form) { // NOSONAR java:S110 — profundidade herdada do Wicket
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    enderecoService.criar(clienteId, new EnderecoCreateDto(
                            formData.logradouro,
                            formData.numero,
                            formData.cep,
                            formData.bairro,
                            emptyToNull(formData.telefone),
                            formData.cidade,
                            formData.estado,
                            formData.enderecoPrincipal,
                            emptyToNull(formData.complemento)
                    ));
                    limpar();
                    mostrarToast(target, "Endereço adicionado com sucesso");
                    ocultarModal(target);
                    onAdicionado(target);
                } catch (RuntimeException e) {
                    AdicionarEnderecoModal.this.error(e.getMessage());
                    target.add(feedback);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedback);
            }
        });
    }

    private void limpar() {
        formData.logradouro        = "";
        formData.numero            = "";
        formData.cep               = "";
        formData.bairro            = "";
        formData.telefone          = "";
        formData.cidade            = "";
        formData.estado            = "";
        formData.enderecoPrincipal = Boolean.FALSE;
        formData.complemento       = "";
    }

    private static String emptyToNull(String value) {
        return value != null && !value.isBlank() ? value : null;
    }

    private void mostrarToast(AjaxRequestTarget target, String mensagem) {
        target.appendJavaScript(
            "(function(){" +
            "var wrap=document.createElement('div');" +
            "wrap.style.cssText='position:fixed;bottom:1.5rem;right:1.5rem;z-index:11000;';" +
            "var t=document.createElement('div');" +
            "t.className='toast align-items-center border-0';" +
            "t.style.cssText='background:var(--erp-success);color:var(--erp-bg);';" +
            "t.setAttribute('role','alert');" +
            "t.innerHTML='<div class=\"d-flex\"><div class=\"toast-body fw-medium\">" + mensagem + "</div>" +
            "<button type=\"button\" class=\"btn-close me-2 m-auto\" data-bs-dismiss=\"toast\"></button></div>';" +
            "wrap.appendChild(t);document.body.appendChild(wrap);" +
            "var bsT=new bootstrap.Toast(t,{delay:4000});bsT.show();" +
            "t.addEventListener('hidden.bs.toast',function(){wrap.remove();});" +
            "})();"
        );
    }

    private void ocultarModal(AjaxRequestTarget target) {
        target.appendJavaScript(
            "(function(){var el=document.getElementById('" + getMarkupId() + "');if(el)bootstrap.Modal.getOrCreateInstance(el).hide();})();"
        );
    }

    protected void onAdicionado(AjaxRequestTarget target) { // hook para subclasses recarregarem a lista
    }
}
