package com.thiago.desafio_estagio.shared.wicket.components;

import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
import com.thiago.desafio_estagio.endereco.application.EnderecoService;
import com.thiago.desafio_estagio.endereco.application.EnderecoUpdateDto;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.UUID;

public class EditarEnderecoModal extends Panel {

    @SpringBean
    private EnderecoService enderecoService;

    private UUID enderecoId;
    private final Model<String> numeroModel      = Model.of("");
    private final Model<String> telefoneModel    = Model.of("");
    private final Model<String> complementoModel = Model.of("");
    private final Model<Boolean> principalModel  = Model.of(Boolean.FALSE);
    private final FeedbackPanel feedback;

    public EditarEnderecoModal(String id) {
        super(id);
        setOutputMarkupId(true);

        Form<Void> form = new Form<>("form");
        add(form);

        feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        form.add(new TextField<>("numero", numeroModel));
        form.add(new TextField<>("telefone", telefoneModel));
        form.add(new TextField<>("complemento", complementoModel));
        form.add(new CheckBox("enderecoPrincipal", principalModel));

        form.add(new AjaxButton("salvar", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    enderecoService.atualizar(enderecoId, new EnderecoUpdateDto(
                            emptyToNull(numeroModel.getObject()),
                            emptyToNull(telefoneModel.getObject()),
                            principalModel.getObject(),
                            emptyToNull(complementoModel.getObject())
                    ));
                    mostrarToast(target, "Endereço atualizado com sucesso");
                    ocultarModal(target);
                    onAtualizado(target);
                } catch (RuntimeException e) {
                    EditarEnderecoModal.this.error(e.getMessage());
                    target.add(feedback);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedback);
            }
        });
    }

    public void setEndereco(EnderecoDto endereco) {
        this.enderecoId = endereco.id();
        numeroModel.setObject(endereco.numero() != null ? endereco.numero() : "");
        telefoneModel.setObject(endereco.telefone() != null ? endereco.telefone() : "");
        complementoModel.setObject(endereco.complemento() != null ? endereco.complemento() : "");
        principalModel.setObject(endereco.enderecoPrincipal());
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

    protected void onAtualizado(AjaxRequestTarget target) {}
}
