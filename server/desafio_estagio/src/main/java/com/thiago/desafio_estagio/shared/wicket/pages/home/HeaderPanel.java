package com.thiago.desafio_estagio.shared.wicket.pages.home;

import com.thiago.desafio_estagio.cliente.application.ClienteService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class HeaderPanel extends Panel {

    @SpringBean
    private ClienteService clienteService;

    public HeaderPanel(String id) {
        super(id);

        long total = clienteService.contarTotal();
        long ativos = clienteService.contarAtivos();

        add(new Label("totalRegistros", String.format("%03d", total)));
        add(new Label("totalAtivos", String.format("%03d", ativos)));
    }
}
