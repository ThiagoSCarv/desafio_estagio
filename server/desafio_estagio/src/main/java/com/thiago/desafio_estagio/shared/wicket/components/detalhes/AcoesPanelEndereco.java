package com.thiago.desafio_estagio.shared.wicket.components.detalhes;

import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class AcoesPanelEndereco extends Panel {

    private final EditarEnderecoModal modalEditar;
    private final ExcluirEnderecoModal modalExcluir;

    public AcoesPanelEndereco(String id, IModel<EnderecoDto> model) {
        super(id, model);

        modalEditar = new EditarEnderecoModal("modalEditar") {
            @Override
            protected void onAtualizado(AjaxRequestTarget target) {
                onEnderecoAtualizado(target);
            }
        };
        add(modalEditar);

        modalExcluir = new ExcluirEnderecoModal("modalExcluir") {
            @Override
            protected void onExcluido(AjaxRequestTarget target) {
                onEnderecoExcluido(target);
            }
        };
        add(modalExcluir);

        add(new AjaxLink<EnderecoDto>("editar", model) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                modalEditar.setEndereco(getModelObject());
                target.add(modalEditar);
                mostrarModal(target, modalEditar);
            }
        });

        add(new AjaxLink<EnderecoDto>("excluir", model) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                modalExcluir.setEndereco(getModelObject());
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

    protected void onEnderecoAtualizado(AjaxRequestTarget target) {}

    protected void onEnderecoExcluido(AjaxRequestTarget target) {}
}
