package com.thiago.desafio_estagio.shared.wicket.components;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClienteService;
import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
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

    private static final String MODAL_CLEANUP_JS =
        "document.querySelectorAll('.modal-backdrop').forEach(function(b){b.remove();});" +
        "document.body.classList.remove('modal-open');" +
        "document.body.style.removeProperty('overflow');" +
        "document.body.style.removeProperty('padding-right');";

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
                item.add(new Label("cep", formatarCep(e.cep())));
                item.add(new Label("cidadeUf", formatarCidadeUf(e.cidade(), e.estado())));
                item.add(new Label("telefone", e.telefone() != null ? formatarTelefone(e.telefone()) : "—"));
                item.add(new Label("complemento", e.complemento() != null ? e.complemento() : "—"));
            }
        };
        add(listaEnderecos);
    }

    public void recarregarLista(AjaxRequestTarget target) {
        clienteModel.detach();
        target.appendJavaScript(MODAL_CLEANUP_JS);
        target.add(this);
    }

    private static String formatarCidadeUf(String cidade, String estado) {
        if (cidade != null && estado != null) return cidade + " · " + estado;
        if (cidade != null) return cidade;
        if (estado != null) return estado;
        return "—";
    }

    private static String formatarCep(String cep) {
        if (cep == null || cep.length() != 8) return cep != null ? cep : "—";
        return cep.substring(0, 5) + "-" + cep.substring(5);
    }

    private static String formatarTelefone(String tel) {
        if (tel == null) return "—";
        if (tel.length() == 11)
            return "(" + tel.substring(0, 2) + ") " + tel.substring(2, 7) + "-" + tel.substring(7);
        if (tel.length() == 10)
            return "(" + tel.substring(0, 2) + ") " + tel.substring(2, 6) + "-" + tel.substring(6);
        return tel;
    }
}
