package com.thiago.desafio_estagio.shared.wicket.components;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.Objects;
import java.util.UUID;

public class RelatorioClienteModal extends Panel {

    private UUID clienteId;

    public RelatorioClienteModal(String id) {
        super(id);
        setOutputMarkupId(true);

        add(new AjaxLink<Void>("gerarPdf") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Objects.requireNonNull(clienteId, "setCliente() deve ser chamado antes de abrir o modal");
                target.appendJavaScript(
                    "window.open('/relatorio/clientes/" + clienteId + "?formato=pdf', '_blank');" +
                    "bootstrap.Modal.getOrCreateInstance(document.getElementById('" + RelatorioClienteModal.this.getMarkupId() + "')).hide();"
                );
            }
        });

        add(new AjaxLink<Void>("gerarXlsx") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Objects.requireNonNull(clienteId, "setCliente() deve ser chamado antes de abrir o modal");
                target.appendJavaScript(
                    "window.open('/relatorio/clientes/" + clienteId + "?formato=xlsx', '_blank');" +
                    "bootstrap.Modal.getOrCreateInstance(document.getElementById('" + RelatorioClienteModal.this.getMarkupId() + "')).hide();"
                );
            }
        });
    }

    public void setCliente(ClienteDto cliente) {
        this.clienteId = cliente.id();
    }
}
