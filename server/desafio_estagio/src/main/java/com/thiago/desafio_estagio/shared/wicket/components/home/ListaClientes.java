package com.thiago.desafio_estagio.shared.wicket.components.home;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjDto;
import com.thiago.desafio_estagio.cliente.application.ClienteService;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import org.apache.wicket.AttributeModifier;
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

    // Wicket substitui o HTML do painel antes de executar qualquer JS do Ajax.
    // Esse trecho limpa o estado do Bootstrap diretamente no DOM após a substituição.
    private static final String MODAL_CLEANUP_JS =
        "document.querySelectorAll('.modal-backdrop').forEach(function(b){b.remove();});" +
        "document.body.classList.remove('modal-open');" +
        "document.body.style.removeProperty('overflow');" +
        "document.body.style.removeProperty('padding-right');";

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

    public void recarregarLista(AjaxRequestTarget target) {
        paginaAtual = 0;
        pageModel.detach();
        target.appendJavaScript(MODAL_CLEANUP_JS);
        target.add(this);
        onAtualizou(target);
    }

    // Ponto de extensão: subclasses sobrescrevem para reagir a mutações (ex: atualizar o HeaderPanel).
    protected void onAtualizou(AjaxRequestTarget target) {
        // implementação opcional — sem comportamento padrão
    }

    private static String formatarDocumento(String doc) {
        String digits = doc.replaceAll("\\D", "");
        if (digits.length() == 11) {
            return digits.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        } else if (digits.length() == 14) {
            return digits.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }
        return doc;
    }

    public ListaClientes(String id) {
        super(id);
        setOutputMarkupId(true);

        add(new FiltroClientesPanel("filtroPanel", nomeFilter, documentoFilter, tipoFiltroModel) {
            @Override
            protected void onFiltroMudou(AjaxRequestTarget target) {
                paginaAtual = 0;
                pageModel.detach();
                target.add(ListaClientes.this);
            }
        });

        WebMarkupContainer listaOrdenada = new WebMarkupContainer("listaOrdenada");
        add(listaOrdenada);

        listaOrdenada.add(new ListView<ClienteDto>("clientes", new LoadableDetachableModel<List<ClienteDto>>() {
            @Override
            protected List<ClienteDto> load() {
                return pageModel.getObject().getContent();
            }
        }) {
            @Override
            protected void populateItem(ListItem<ClienteDto> item) {
                ClienteDto dto = item.getModelObject();

                String nome;
                String tipo;
                String documento;
                if (dto instanceof ClientePfDto pf) {
                    nome = pf.nome();
                    tipo = "PF";
                    documento = pf.cpf();
                } else {
                    ClientePjDto pj = (ClientePjDto) dto;
                    nome = pj.razaoSocial();
                    tipo = "PJ";
                    documento = pj.cnpj();
                }

                item.add(new Label("clienteNome", nome));
                item.add(new Label("clienteTipo", tipo));
                item.add(new Label("clienteDocumento", formatarDocumento(documento)));
                item.add(new Label("clienteEmail", dto.email()));

                boolean ativo = dto.ativo();
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

        add(new Label("paginacaoInfo", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                Page<ClienteDto> page = pageModel.getObject();
                return String.format("%02d / %02d", paginaAtual + 1, Math.max(1, page.getTotalPages()));
            }
        }));

        add(new AjaxLink<Void>("paginaAnterior") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (paginaAtual > 0) {
                    paginaAtual--;
                    pageModel.detach();
                    target.add(ListaClientes.this);
                }
            }
        });

        add(new AjaxLink<Void>("proximaPagina") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (paginaAtual < pageModel.getObject().getTotalPages() - 1) {
                    paginaAtual++;
                    pageModel.detach();
                    target.add(ListaClientes.this);
                }
            }
        });
    }
}
