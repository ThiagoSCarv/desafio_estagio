package com.thiago.desafio_estagio.shared.wicket.components.detalhes;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClienteService;
import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
import com.thiago.desafio_estagio.shared.wicket.util.WicketUtil;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collections;
import java.util.UUID;

public class ListaEnderecosCliente extends Panel {

    @SpringBean
    private ClienteService clienteService;

    private final LoadableDetachableModel<ClienteDto> clienteModel;

    public ListaEnderecosCliente(String id, UUID clienteId) {
        super(id);
        setOutputMarkupId(true);

        clienteModel = new LoadableDetachableModel<>() {
            @Override
            protected ClienteDto load() {
                return clienteService.buscarPorId(clienteId);
            }
        };

        ListView<EnderecoDto> listaEnderecos = new ListView<>("enderecos", () ->
                clienteModel.getObject().enderecos() != null
                        ? clienteModel.getObject().enderecos()
                        : Collections.emptyList()) {
            @Override
            protected void populateItem(ListItem<EnderecoDto> item) {
                item.add(new EnderecoItemPanel("itemPanel", item.getModel()) {
                    @Override
                    protected void onEnderecoAtualizado(AjaxRequestTarget target) {
                        recarregarLista(target);
                    }

                    @Override
                    protected void onEnderecoExcluido(AjaxRequestTarget target) {
                        recarregarLista(target);
                    }
                });
            }
        };
        add(listaEnderecos);
    }

    public void recarregarLista(AjaxRequestTarget target) {
        clienteModel.detach();
        target.appendJavaScript(WicketUtil.MODAL_CLEANUP_JS);
        target.add(this);
    }


}
