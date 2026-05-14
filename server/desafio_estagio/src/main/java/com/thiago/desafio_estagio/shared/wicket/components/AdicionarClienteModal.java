package com.thiago.desafio_estagio.shared.wicket.components;

import com.thiago.desafio_estagio.cliente.application.ClientePfCreateDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfService;
import com.thiago.desafio_estagio.cliente.application.ClientePjCreateDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjService;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdicionarClienteModal extends Panel {

    @SpringBean
    private ClientePfService clientePfService;

    @SpringBean
    private ClientePjService clientePjService;

    private TipoPessoa tipoSelecionado = TipoPessoa.FISICA;

    private final Model<String> emailModel             = Model.of("");
    private final Model<String> nomeModel              = Model.of("");
    private final Model<String> cpfModel               = Model.of("");
    private final Model<String> rgModel                = Model.of("");
    private final Model<String> dataNascimentoModel    = Model.of("");
    private final Model<String> cnpjModel              = Model.of("");
    private final Model<String> razaoSocialModel       = Model.of("");
    private final Model<String> inscricaoEstadualModel = Model.of("");
    private final Model<String> dataCriacaoModel       = Model.of("");

    private final WebMarkupContainer pfCampos;
    private final WebMarkupContainer pjCampos;
    private final FeedbackPanel feedback;

    public AdicionarClienteModal(String id) {
        super(id);
        setOutputMarkupId(true);

        Form<Void> form = new Form<>("form");
        add(form);

        feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        pfCampos = new WebMarkupContainer("pfCampos");
        pfCampos.setOutputMarkupPlaceholderTag(true);
        pfCampos.add(new TextField<>("nome", nomeModel));
        pfCampos.add(new TextField<>("cpf", cpfModel));
        pfCampos.add(new TextField<>("rg", rgModel));
        pfCampos.add(new TextField<>("dataNascimento", dataNascimentoModel));

        pjCampos = new WebMarkupContainer("pjCampos");
        pjCampos.setOutputMarkupPlaceholderTag(true);
        pjCampos.setVisible(false);
        pjCampos.add(new TextField<>("cnpj", cnpjModel));
        pjCampos.add(new TextField<>("razaoSocial", razaoSocialModel));
        pjCampos.add(new TextField<>("inscricaoEstadual", inscricaoEstadualModel));
        pjCampos.add(new TextField<>("dataCriacao", dataCriacaoModel));

        form.add(new AjaxLink<Void>("btnPf") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                tipoSelecionado = TipoPessoa.FISICA;
                pfCampos.setVisible(true);
                pjCampos.setVisible(false);
                target.add(pfCampos, pjCampos);
                ativarTab(target, "FISICA");
            }
        });

        form.add(new AjaxLink<Void>("btnPj") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                tipoSelecionado = TipoPessoa.JURIDICA;
                pfCampos.setVisible(false);
                pjCampos.setVisible(true);
                target.add(pfCampos, pjCampos);
                ativarTab(target, "JURIDICA");
            }
        });

        form.add(new EmailTextField("email", emailModel));
        form.add(pfCampos);
        form.add(pjCampos);

        form.add(new AjaxButton("salvar", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    criar();
                    limpar(target);
                    ocultarModal(target);
                    onAdicionado(target);
                } catch (RuntimeException e) {
                    AdicionarClienteModal.this.error(e.getMessage());
                    target.add(feedback);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedback);
            }
        });
    }

    private void criar() {
        if (tipoSelecionado == TipoPessoa.FISICA) {
            clientePfService.criar(new ClientePfCreateDto(
                    emailModel.getObject(),
                    nomeModel.getObject(),
                    cpfModel.getObject(),
                    rgModel.getObject(),
                    parseDate(dataNascimentoModel.getObject()),
                    null
            ));
        } else {
            clientePjService.criar(new ClientePjCreateDto(
                    emailModel.getObject(),
                    cnpjModel.getObject(),
                    razaoSocialModel.getObject(),
                    inscricaoEstadualModel.getObject(),
                    parseDate(dataCriacaoModel.getObject()),
                    null
            ));
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Data obrigatória não informada.");
        }
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de data inválido. Use dd/mm/aaaa.");
        }
    }

    private void limpar(AjaxRequestTarget target) {
        tipoSelecionado = TipoPessoa.FISICA;
        emailModel.setObject("");
        nomeModel.setObject("");
        cpfModel.setObject("");
        rgModel.setObject("");
        dataNascimentoModel.setObject("");
        cnpjModel.setObject("");
        razaoSocialModel.setObject("");
        inscricaoEstadualModel.setObject("");
        dataCriacaoModel.setObject("");
        pfCampos.setVisible(true);
        pjCampos.setVisible(false);
        target.add(pfCampos, pjCampos);
        ativarTab(target, "FISICA");
    }

    private void ativarTab(AjaxRequestTarget target, String tipo) {
        target.appendJavaScript(
            "(function(){" +
            "var m=document.getElementById('" + getMarkupId() + "');" +
            "m.querySelectorAll('[data-tipo]').forEach(function(b){b.classList.remove('erp-tipo-btn--ativo');});" +
            "var a=m.querySelector('[data-tipo=\"" + tipo + "\"]');" +
            "if(a)a.classList.add('erp-tipo-btn--ativo');" +
            "})();"
        );
    }

    private void ocultarModal(AjaxRequestTarget target) {
        target.appendJavaScript(
            "(function(){var el=document.getElementById('" + getMarkupId() + "');if(el)bootstrap.Modal.getOrCreateInstance(el).hide();})();"
        );
    }

    protected void onAdicionado(AjaxRequestTarget target) {}
}
