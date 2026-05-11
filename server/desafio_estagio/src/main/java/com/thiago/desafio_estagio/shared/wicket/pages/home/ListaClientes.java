package com.thiago.desafio_estagio.shared.wicket.pages.home;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjDto;
import com.thiago.desafio_estagio.cliente.application.ClienteService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class ListaClientes extends Panel {

    @SpringBean
    private ClienteService clienteService;

    public ListaClientes(String id) {
        super(id);

        Page<ClienteDto> pagina = clienteService.listarTodos(null, null, null, PageRequest.of(0, 12));

        add(new ListView<ClienteDto>("clientes", pagina.getContent()) {
            @Override
            protected void populateItem(ListItem<ClienteDto> item) {
                ClienteDto dto = item.getModelObject();

                String nome = dto instanceof ClientePfDto pf
                        ? pf.nome()
                        : ((ClientePjDto) dto).razaoSocial();

                boolean ativo = dto.ativo();

                item.add(new Label("clienteNome", nome));

                WebMarkupContainer clienteStatus = new WebMarkupContainer("clienteStatus");
                clienteStatus.add(AttributeModifier.replace("class",
                    "erp-status " + (ativo ? "erp-status--ativo" : "erp-status--inativo")));
                clienteStatus.add(new Label("statusTexto", ativo ? "Ativo" : "Inativo"));
                item.add(clienteStatus);
            }
        });
    }
}
