package com.thiago.desafio_estagio.shared.wicket.components;

import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
import com.thiago.desafio_estagio.endereco.application.EnderecoService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.UUID;

public class ExcluirEnderecoModal extends Panel {

    @SpringBean
    private EnderecoService enderecoService;

    private UUID enderecoId;
    private final Model<String> logradouroModel = Model.of("");
    private final FeedbackPanel feedback;

    public ExcluirEnderecoModal(String id) {
        super(id);
        setOutputMarkupId(true);

        feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        add(feedback);

        add(new Label("logradouroEndereco", logradouroModel));

        add(new AjaxLink<Void>("confirmar") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    enderecoService.deletar(enderecoId);
                    ocultarModal(target);
                    onExcluido(target);
                } catch (RuntimeException e) {
                    ExcluirEnderecoModal.this.error(e.getMessage());
                    target.add(feedback);
                }
            }
        });
    }

    public void setEndereco(EnderecoDto endereco) {
        this.enderecoId = endereco.id();
        String logradouro = endereco.logradouro() != null ? endereco.logradouro() : "Endereço";
        if (endereco.numero() != null && !endereco.numero().isBlank())
            logradouro += ", " + endereco.numero();
        logradouroModel.setObject(logradouro);
    }

    private void ocultarModal(AjaxRequestTarget target) {
        target.appendJavaScript(
            "(function(){var el=document.getElementById('" + getMarkupId() + "');if(el)bootstrap.Modal.getOrCreateInstance(el).hide();})();"
        );
    }

    protected void onExcluido(AjaxRequestTarget target) {}
}
