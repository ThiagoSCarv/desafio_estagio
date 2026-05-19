package com.thiago.desafio_estagio.shared.wicket.components.home;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjDto;
import com.thiago.desafio_estagio.shared.utils.DocumentFormat;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class ClienteItemPanel extends Panel {

    public ClienteItemPanel(String id, IModel<ClienteDto> model) {
        super(id, model);
        ClienteDto dto = model.getObject();

        String nome;
        String tipo;
        String documento;
        if (dto instanceof ClientePfDto pf) {
            nome = pf.nome();
            tipo = "PF";
            documento = pf.cpf();
        } else {
            ClientePjDto pj = (ClientePjDto) dto;
            nome = pj.razaoSocial();
            tipo = "PJ";
            documento = pj.cnpj();
        }

        add(new Label("clienteNome", nome));
        add(new Label("clienteTipo", tipo));
        add(new Label("clienteDocumento", DocumentFormat.formatDocument(documento)));
        add(new Label("clienteEmail", dto.email()));

        boolean ativo = dto.ativo();
        WebMarkupContainer clienteStatus = new WebMarkupContainer("clienteStatus");
        clienteStatus.add(AttributeModifier.replace("class",
            "erp-status " + (ativo ? "erp-status--ativo" : "erp-status--inativo")));
        clienteStatus.add(new Label("statusTexto", ativo ? "Ativo" : "Inativo"));
        add(clienteStatus);

        add(new AcoesPanel("acoes", model) {
            @Override
            protected void onClienteExcluido(AjaxRequestTarget target) {
                ClienteItemPanel.this.onClienteExcluido(target);
            }

            @Override
            protected void onClienteAtualizado(AjaxRequestTarget target) {
                ClienteItemPanel.this.onClienteAtualizado(target);
            }
        });
    }

    protected abstract void onClienteExcluido(AjaxRequestTarget target);

    protected abstract void onClienteAtualizado(AjaxRequestTarget target);
}
