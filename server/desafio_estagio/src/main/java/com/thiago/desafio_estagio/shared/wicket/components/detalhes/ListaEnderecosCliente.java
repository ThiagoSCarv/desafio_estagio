package com.thiago.desafio_estagio.shared.wicket.components.detalhes;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClienteService;
import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
import com.thiago.desafio_estagio.shared.utils.DocumentFormat;
import com.thiago.desafio_estagio.shared.wicket.util.WicketUtil;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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
                EnderecoDto e = item.getModelObject();

                WebMarkupContainer principalBadge = new WebMarkupContainer("principalBadge");
                principalBadge.setVisible(e.enderecoPrincipal());
                item.add(principalBadge);

                item.add(new AcoesPanelEndereco("acoes", item.getModel()) {
                    @Override
                    protected void onEnderecoAtualizado(AjaxRequestTarget target) {
                        recarregarLista(target);
                    }

                    @Override
                    protected void onEnderecoExcluido(AjaxRequestTarget target) {
                        recarregarLista(target);
                    }
                });

                String logradouroCompleto = e.logradouro() != null ? e.logradouro() : "—";
                if (e.numero() != null && !e.numero().isBlank())
                    logradouroCompleto += ", " + e.numero();
                item.add(new Label("logradouro", logradouroCompleto));

                item.add(new Label("bairro", e.bairro() != null ? e.bairro() : "—"));
                item.add(new Label("cep", DocumentFormat.formatarCep(e.cep())));
                item.add(new Label("cidadeUf", formatarCidadeUf(e.cidade(), e.estado())));
                item.add(new Label("telefone", e.telefone() != null ? DocumentFormat.formatarTelefone(e.telefone()) : "—"));
                item.add(new Label("complemento", e.complemento() != null ? e.complemento() : "—"));
            }
        };
        add(listaEnderecos);
    }

    public void recarregarLista(AjaxRequestTarget target) {
        clienteModel.detach();
        target.appendJavaScript(WicketUtil.MODAL_CLEANUP_JS);
        target.add(this);
    }

    private static String formatarCidadeUf(String cidade, String estado) {
        if (cidade != null && estado != null) return cidade + " · " + estado;
        if (cidade != null) return cidade;
        if (estado != null) return estado;
        return "—";
    }

}
