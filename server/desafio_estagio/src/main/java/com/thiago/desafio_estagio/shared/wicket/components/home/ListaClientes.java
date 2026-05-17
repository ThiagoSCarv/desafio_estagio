package com.thiago.desafio_estagio.shared.wicket.components.home;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClienteService;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.shared.wicket.util.WicketUtil;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class ListaClientes extends Panel {

    @SpringBean
    private ClienteService clienteService;

    private static final int PAGE_SIZE = 12;

    private final Model<String> nomeFilter      = Model.of("");
    private final Model<String> documentoFilter  = Model.of("");
    private final Model<TipoPessoa> tipoFiltroModel = Model.of((TipoPessoa) null);
    private int paginaAtual                     = 0;

    // Modelo compartilhado entre ListView e Label de paginação dentro do mesmo request.
    // O campo transiente do LoadableDetachableModel garante re-carregamento entre requests.
    private final LoadableDetachableModel<Page<ClienteDto>> pageModel =
        new LoadableDetachableModel<>() {
            @Override
            protected Page<ClienteDto> load() {
                return clienteService.listarTodos(
                    tipoFiltroModel.getObject(),
                    documentoFilter.getObject(),
                    nomeFilter.getObject(),
                    PageRequest.of(paginaAtual, PAGE_SIZE)
                );
            }
        };

    // Container que agrupa lista + paginação — atualizado isoladamente pelo filtro
    // para evitar re-renderizar o FiltroClientesPanel e mover o cursor do input.
    private final WebMarkupContainer resultados = new WebMarkupContainer("resultados");

    public void recarregarLista(AjaxRequestTarget target) {
        paginaAtual = 0;
        pageModel.detach();
        target.appendJavaScript(WicketUtil.MODAL_CLEANUP_JS);
        target.add(this);
        onAtualizou(target);
    }

    // Ponto de extensão: subclasses sobrescrevem para reagir a mutações (ex: atualizar o HeaderPanel).
    protected void onAtualizou(AjaxRequestTarget target) {
        // implementação opcional — sem comportamento padrão
    }

    public ListaClientes(String id) {
        super(id);
        setOutputMarkupId(true);

        add(new FiltroClientesPanel("filtroPanel", nomeFilter, documentoFilter, tipoFiltroModel) {
            @Override
            protected void onFiltroMudou(AjaxRequestTarget target) {
                paginaAtual = 0;
                pageModel.detach();
                target.add(resultados);
            }
        });

        resultados.setOutputMarkupId(true);
        add(resultados);

        WebMarkupContainer listaOrdenada = new WebMarkupContainer("listaOrdenada");
        resultados.add(listaOrdenada);

        listaOrdenada.add(new ListView<ClienteDto>("clientes", new LoadableDetachableModel<List<ClienteDto>>() {
            @Override
            protected List<ClienteDto> load() {
                return pageModel.getObject().getContent();
            }
        }) {
            @Override
            protected void populateItem(ListItem<ClienteDto> item) {
                item.add(new ClienteItemPanel("itemPanel", item.getModel()) {
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

        resultados.add(new Label("paginacaoInfo", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                Page<ClienteDto> page = pageModel.getObject();
                return String.format("%02d / %02d", paginaAtual + 1, Math.max(1, page.getTotalPages()));
            }
        }));

        resultados.add(new AjaxLink<Void>("paginaAnterior") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (paginaAtual > 0) {
                    paginaAtual--;
                    pageModel.detach();
                    target.add(resultados);
                }
            }
        });

        resultados.add(new AjaxLink<Void>("proximaPagina") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (paginaAtual < pageModel.getObject().getTotalPages() - 1) {
                    paginaAtual++;
                    pageModel.detach();
                    target.add(resultados);
                }
            }
        });
    }
}
