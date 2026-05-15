package com.thiago.desafio_estagio.shared.wicket.components.home;

import com.thiago.desafio_estagio.cliente.application.ClienteService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class HeaderPanel extends Panel {

    @SpringBean
    private ClienteService clienteService;

    public HeaderPanel(String id) {
        super(id);
        setOutputMarkupId(true);

        // LoadableDetachableModel garante que os contadores sejam relidos a cada re-render Ajax
        add(new Label("totalRegistros", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return String.format("%03d", clienteService.contarTotal());
            }
        }));

        add(new Label("totalAtivos", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return String.format("%03d", clienteService.contarAtivos());
            }
        }));
    }
}
