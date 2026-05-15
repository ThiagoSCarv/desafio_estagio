package com.thiago.desafio_estagio.shared.wicket.components;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class AcoesPanel extends Panel {

    private final ExcluirClienteModal modalExcluir;
    private final EditarClienteModal modalEditar;
    private final RelatorioClienteModal modalRelatorio;

    public AcoesPanel(String id, IModel<ClienteDto> model) {
        super(id, model);

        modalExcluir = new ExcluirClienteModal("modalExcluir") {
            @Override
            protected void onExcluido(AjaxRequestTarget target) {
                onClienteExcluido(target);
            }
        };
        add(modalExcluir);

        modalEditar = new EditarClienteModal("modalEditar") {
            @Override
            protected void onAtualizado(AjaxRequestTarget target) {
                onClienteAtualizado(target);
            }
        };
        add(modalEditar);

        modalRelatorio = new RelatorioClienteModal("modalRelatorio");
        add(modalRelatorio);

        add(new AjaxLink<ClienteDto>("detalhes", model) {
            @Override
            public void onClick(AjaxRequestTarget target) {}
        });

        add(new AjaxLink<ClienteDto>("editar", model) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                modalEditar.setCliente(getModelObject());
                target.add(modalEditar);
                mostrarModal(target, modalEditar);
            }
        });

        add(new AjaxLink<ClienteDto>("relatorio", model) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                modalRelatorio.setCliente(getModelObject());
                target.add(modalRelatorio);
                mostrarModal(target, modalRelatorio);
            }
        });

        add(new AjaxLink<ClienteDto>("excluir", model) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                modalExcluir.setCliente(getModelObject());
                target.add(modalExcluir);
                mostrarModal(target, modalExcluir);
            }
        });
    }

    private void mostrarModal(AjaxRequestTarget target, Panel modal) {
        target.appendJavaScript(
            "bootstrap.Modal.getOrCreateInstance(document.getElementById('" + modal.getMarkupId() + "')).show();"
        );
    }

    protected void onClienteExcluido(AjaxRequestTarget target) {}

    protected void onClienteAtualizado(AjaxRequestTarget target) {}
}