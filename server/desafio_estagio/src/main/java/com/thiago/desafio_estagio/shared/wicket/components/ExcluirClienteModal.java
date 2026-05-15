package com.thiago.desafio_estagio.shared.wicket.components;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjDto;
import com.thiago.desafio_estagio.cliente.application.ClienteService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.UUID;

public class ExcluirClienteModal extends Panel {

    @SpringBean
    private ClienteService clienteService;

    private UUID clienteId;
    private final Model<String> nomeModel = Model.of("");
    private final FeedbackPanel feedback;

    public ExcluirClienteModal(String id) {
        super(id);
        setOutputMarkupId(true);

        feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        add(feedback);

        add(new Label("nomeCliente", nomeModel));

        add(new AjaxLink<Void>("confirmar") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    clienteService.deletar(clienteId);
                    ocultarModal(target);
                    mostrarToast(target, "Cliente removido com sucesso.");
                    onExcluido(target);
                } catch (RuntimeException e) {
                    ExcluirClienteModal.this.error(e.getMessage());
                    target.add(feedback);
                }
            }
        });
    }

    public void setCliente(ClienteDto cliente) {
        this.clienteId = cliente.id();
        String nome = cliente instanceof ClientePfDto pf
                ? pf.nome()
                : ((ClientePjDto) cliente).razaoSocial();
        nomeModel.setObject(nome);
    }

    private void ocultarModal(AjaxRequestTarget target) {
        target.appendJavaScript(
            "(function(){var el=document.getElementById('" + getMarkupId() + "');if(el)bootstrap.Modal.getOrCreateInstance(el).hide();})();"
        );
    }

    private void mostrarToast(AjaxRequestTarget target, String mensagem) {
        target.appendJavaScript(
            "(function(){" +
            "var c=document.getElementById('erp-toast-container');" +
            "if(!c){c=document.createElement('div');c.id='erp-toast-container';" +
            "c.className='toast-container position-fixed bottom-0 end-0 p-3';document.body.appendChild(c);}" +
            "var t=document.createElement('div');" +
            "t.className='toast align-items-center text-bg-success border-0';" +
            "t.setAttribute('role','alert');t.setAttribute('aria-atomic','true');" +
            "t.innerHTML='<div class=\"d-flex\"><div class=\"toast-body\">" + mensagem + "</div>" +
            "<button type=\"button\" class=\"btn-close btn-close-white me-2 m-auto\" data-bs-dismiss=\"toast\"></button></div>';" +
            "c.appendChild(t);" +
            "var b=new bootstrap.Toast(t,{delay:3000});b.show();" +
            "t.addEventListener('hidden.bs.toast',function(){c.removeChild(t);});" +
            "})();"
        );
    }

    protected void onExcluido(AjaxRequestTarget target) {}
}
