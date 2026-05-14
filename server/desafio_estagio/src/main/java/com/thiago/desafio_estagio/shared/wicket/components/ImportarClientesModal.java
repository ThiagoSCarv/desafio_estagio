package com.thiago.desafio_estagio.shared.wicket.components;

import com.thiago.desafio_estagio.cliente.application.ImportacaoResultado;
import com.thiago.desafio_estagio.cliente.application.ImportacaoService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

public class ImportarClientesModal extends Panel {

    @SpringBean
    private ImportacaoService importacaoService;

    private final Model<String> resumoModel = Model.of("");
    private final IModel<List<String>> errosModel = Model.ofList(new ArrayList<>());

    public ImportarClientesModal(String id) {
        super(id);

        Form<Void> form = new Form<>("form");
        form.setMultiPart(true);
        add(form);

        FeedbackPanel feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        FileUploadField arquivo = new FileUploadField("arquivo");
        arquivo.setRequired(true);
        form.add(arquivo);

        form.add(new ExternalLink("downloadTemplate", "/clientes/importar/template"));

        WebMarkupContainer resultado = new WebMarkupContainer("resultado");
        resultado.setOutputMarkupPlaceholderTag(true);
        resultado.setVisible(false);
        form.add(resultado);

        resultado.add(new Label("resumo", resumoModel));
        resultado.add(new ListView<String>("listaErros", errosModel) {
            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(new Label("erroItem", item.getModel()));
            }
        });

        form.add(new AjaxButton("importar") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                FileUpload upload = arquivo.getFileUpload();
                if (upload == null) {
                    error("Selecione um arquivo .xlsx antes de importar.");
                    target.add(feedback);
                    return;
                }
                try {
                    ImportacaoResultado r = importacaoService.importarClientes(upload.getInputStream());
                    errosModel.setObject(r.erros());
                    if (r.criados() > 0) {
                        resumoModel.setObject(r.criados() + " cliente(s) importado(s) com sucesso." +
                            (r.erros().isEmpty() ? "" : " " + r.erros().size() + " erro(s)."));
                        onImportou(target);
                    } else {
                        resumoModel.setObject(r.erros().isEmpty()
                            ? "Nenhuma linha encontrada na planilha."
                            : r.erros().size() + " erro(s) — nenhum cliente importado.");
                    }
                    resultado.setVisible(true);
                    target.add(resultado);
                } catch (Exception e) {
                    error("Falha ao processar o arquivo: " + e.getMessage());
                }
                target.add(feedback);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedback);
            }
        });
    }

    protected void onImportou(AjaxRequestTarget target) {}
}
