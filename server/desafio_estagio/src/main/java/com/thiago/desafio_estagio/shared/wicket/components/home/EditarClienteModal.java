package com.thiago.desafio_estagio.shared.wicket.components.home;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfService;
import com.thiago.desafio_estagio.cliente.application.ClientePfUpdateDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjService;
import com.thiago.desafio_estagio.cliente.application.ClientePjUpdateDto;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.shared.utils.DocumentFormat;
import com.thiago.desafio_estagio.shared.wicket.util.WicketUtil;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.UUID;

public class EditarClienteModal extends Panel {

    @SpringBean
    private ClientePfService clientePfService;

    @SpringBean
    private ClientePjService clientePjService;

    private UUID clienteId;
    private TipoPessoa tipoPessoa;

    private final Model<String> tipoModel          = Model.of("");
    private final Model<String> badgeLabelModel    = Model.of("");
    private final Model<String> badgeNameModel     = Model.of("");
    private final Model<String> emailLabelModel    = Model.of("");
    private final Model<String> ativoNumModel      = Model.of("03");
    private final Model<String> emailModel         = Model.of("");
    private final Model<Boolean> ativoModel        = Model.of(Boolean.TRUE);
    private final Model<String> nomeModel          = Model.of("");
    private final Model<String> razaoSocialModel   = Model.of("");
    private final Model<String> inscricaoEstadualModel = Model.of("");

    private final WebMarkupContainer pfCampos;
    private final WebMarkupContainer pjCampos;
    private final FeedbackPanel feedback;
    private final Form<Void> form;

    public EditarClienteModal(String id) {
        super(id);
        setOutputMarkupId(true);

        add(new Label("titulotipo", tipoModel));

        form = new Form<>("form");
        form.setOutputMarkupId(true);
        add(form);

        feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        form.add(new Label("badgeLabel", badgeLabelModel));
        form.add(new Label("badgeName", badgeNameModel));
        form.add(new Label("emailLabel", emailLabelModel));
        form.add(new Label("ativoNum", ativoNumModel));

        form.add(new EmailTextField("email", emailModel));

        pfCampos = new WebMarkupContainer("pfCampos");
        pfCampos.setOutputMarkupPlaceholderTag(true);
        pfCampos.setVisible(false);
        pfCampos.add(new TextField<>("nome", nomeModel));
        form.add(pfCampos);

        pjCampos = new WebMarkupContainer("pjCampos");
        pjCampos.setOutputMarkupPlaceholderTag(true);
        pjCampos.setVisible(false);
        pjCampos.add(new TextField<>("razaoSocial", razaoSocialModel));
        pjCampos.add(new TextField<>("inscricaoEstadual", inscricaoEstadualModel));
        form.add(pjCampos);

        RadioGroup<Boolean> ativoGroup = new RadioGroup<>("ativoGroup", ativoModel);
        ativoGroup.add(new Radio<>("radioAtivo",  Model.of(Boolean.TRUE)));
        ativoGroup.add(new Radio<>("radioInativo", Model.of(Boolean.FALSE)));
        form.add(ativoGroup);

        AjaxButton cancelar = new AjaxButton("cancelar") { // NOSONAR java:S110 — profundidade herdada do Wicket
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                limpar(target);
                WicketUtil.ocultarModal(target, EditarClienteModal.this);
            }
        };
        // Pula validação — cancelar não deve falhar por campos obrigatórios em branco.
        cancelar.setDefaultFormProcessing(false);
        form.add(cancelar);

        form.add(new AjaxButton("salvar", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    salvar();
                    WicketUtil.mostrarToast(target, "Atualização realizada com sucesso");
                    WicketUtil.ocultarModal(target, EditarClienteModal.this);
                    onAtualizado(target);
                } catch (RuntimeException e) {
                    EditarClienteModal.this.error(e.getMessage());
                    target.add(feedback);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedback);
            }
        });
    }

    public void setCliente(ClienteDto cliente) {
        this.clienteId = cliente.id();
        this.tipoPessoa = cliente.tipoPessoa();
        ativoModel.setObject(cliente.ativo());
        emailModel.setObject(cliente.email());

        if (cliente instanceof ClientePfDto pf) {
            tipoModel.setObject("Pessoa Física");
            badgeLabelModel.setObject("CLIENTE");
            badgeNameModel.setObject(pf.nome());
            emailLabelModel.setObject("E-MAIL");
            ativoNumModel.setObject("03");
            nomeModel.setObject(pf.nome());
            pfCampos.setVisible(true);
            pjCampos.setVisible(false);
        } else {
            ClientePjDto pj = (ClientePjDto) cliente;
            tipoModel.setObject("Pessoa Jurídica");
            badgeLabelModel.setObject("CNPJ");
            badgeNameModel.setObject(DocumentFormat.formatarCnpj(pj.cnpj()));
            emailLabelModel.setObject("E-MAIL CORPORATIVO");
            ativoNumModel.setObject("04");
            razaoSocialModel.setObject(pj.razaoSocial());
            inscricaoEstadualModel.setObject(pj.inscricaoEstadual());
            pfCampos.setVisible(false);
            pjCampos.setVisible(true);
        }
    }

    // Reseta o estado do formulário (Models + cache de input bruto dos componentes) e marca o form para re-render via Ajax.
    private void limpar(AjaxRequestTarget target) {
        clienteId = null;
        tipoPessoa = null;
        tipoModel.setObject("");
        badgeLabelModel.setObject("");
        badgeNameModel.setObject("");
        emailLabelModel.setObject("");
        ativoNumModel.setObject("03");
        emailModel.setObject("");
        ativoModel.setObject(Boolean.TRUE);
        nomeModel.setObject("");
        razaoSocialModel.setObject("");
        inscricaoEstadualModel.setObject("");
        pfCampos.setVisible(false);
        pjCampos.setVisible(false);
        WicketUtil.limparForm(form, target);
    }

    private void salvar() {
        if (tipoPessoa == TipoPessoa.FISICA) {
            clientePfService.atualizar(clienteId, new ClientePfUpdateDto(
                    emailModel.getObject(),
                    nomeModel.getObject(),
                    ativoModel.getObject()
            ));
        } else {
            clientePjService.atualizar(clienteId, new ClientePjUpdateDto(
                    emailModel.getObject(),
                    razaoSocialModel.getObject(),
                    inscricaoEstadualModel.getObject(),
                    ativoModel.getObject()
            ));
        }
    }

    // Ponto de extensão: subclasses ou páginas que instanciam o modal sobrescrevem para reagir ao salvamento.
    protected void onAtualizado(AjaxRequestTarget target) {}
}
