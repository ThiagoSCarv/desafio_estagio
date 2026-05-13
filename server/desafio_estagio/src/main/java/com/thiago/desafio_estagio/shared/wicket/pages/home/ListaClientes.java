package com.thiago.desafio_estagio.shared.wicket.pages.home;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjDto;
import com.thiago.desafio_estagio.cliente.application.ClienteService;
import com.thiago.desafio_estagio.shared.utils.JsUtils;
import com.thiago.desafio_estagio.shared.wicket.components.AcoesPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class ListaClientes extends Panel {

    @SpringBean
    private ClienteService clienteService;

    // Wicket substitui o HTML do painel antes de executar qualquer JS do Ajax.
    // Isso faz com que o elemento do modal já esteja fora do DOM quando o Bootstrap
    // tenta chamá-lo, deixando o backdrop e o overflow do body presos. Esse trecho
    // limpa o estado do Bootstrap diretamente no DOM após a substituição.
    private static final String MODAL_CLEANUP_JS =
        "document.querySelectorAll('.modal-backdrop').forEach(function(b){b.remove();});" +
        "document.body.classList.remove('modal-open');" +
        "document.body.style.removeProperty('overflow');" +
        "document.body.style.removeProperty('padding-right');";

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(JsUtils.MASKS));
    }

    private void recarregarLista(AjaxRequestTarget target) {
        target.appendJavaScript(MODAL_CLEANUP_JS);
        target.add(this);
    }

    public ListaClientes(String id) {
        super(id);
        setOutputMarkupId(true);

        IModel<List<ClienteDto>> clientesModel = new LoadableDetachableModel<>() {
            @Override
            protected List<ClienteDto> load() {
                return clienteService.listarTodos(null, null, null, PageRequest.of(0, 12)).getContent();
            }
        };

        add(new ListView<ClienteDto>("clientes", clientesModel) {
            @Override
            protected void populateItem(ListItem<ClienteDto> item) {
                ClienteDto dto = item.getModelObject();

                String nome = dto instanceof ClientePfDto pf
                        ? pf.nome()
                        : ((ClientePjDto) dto).razaoSocial();

                boolean ativo = dto.ativo();

                String tipo = dto instanceof ClientePfDto ? "PF" : "PJ";

                String documento = dto instanceof ClientePfDto pf2
                        ? pf2.cpf()
                        : ((ClientePjDto) dto).cnpj();

                item.add(new Label("clienteNome", nome));
                item.add(new Label("clienteTipo", tipo));
                item.add(new Label("clienteDocumento", documento));
                item.add(new Label("clienteEmail", dto.email()));

                WebMarkupContainer clienteStatus = new WebMarkupContainer("clienteStatus");
                clienteStatus.add(AttributeModifier.replace("class",
                    "erp-status " + (ativo ? "erp-status--ativo" : "erp-status--inativo")));
                clienteStatus.add(new Label("statusTexto", ativo ? "Ativo" : "Inativo"));
                item.add(clienteStatus);
                item.add(new AcoesPanel("acoes", item.getModel()) {
                    @Override
                    protected void onClienteExcluido(AjaxRequestTarget target) {
                        recarregarLista(target);
                    }

                    @Override
                    protected void onClienteAtualizado(AjaxRequestTarget target) {
                        recarregarLista(target);
                    }
                });
            }
        });
    }
}
