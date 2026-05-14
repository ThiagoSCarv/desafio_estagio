package com.thiago.desafio_estagio.shared.wicket.components;

import com.thiago.desafio_estagio.cliente.application.ClienteDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfService;
import com.thiago.desafio_estagio.cliente.application.ClientePfUpdateDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjService;
import com.thiago.desafio_estagio.cliente.application.ClientePjUpdateDto;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
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

    private final Model<String> breadcrumbModel    = Model.of("");
    private final Model<String> tipoModel          = Model.of("");
    private final Model<String> badgeLabelModel    = Model.of("");
    private final Model<String> badgeNameModel     = Model.of("");
    private final Model<String> badgeCodigoModel   = Model.of("");
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

    public EditarClienteModal(String id) {
        super(id);
        setOutputMarkupId(true);

        add(new Label("breadcrumb", breadcrumbModel));
        add(new Label("titulotipo", tipoModel));

        Form<Void> form = new Form<>("form");
        add(form);

        feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        form.add(new Label("badgeLabel", badgeLabelModel));
        form.add(new Label("badgeName", badgeNameModel));
        form.add(new Label("badgeCodigo", badgeCodigoModel));
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

        form.add(new CheckBox("ativo", ativoModel));

        form.add(new AjaxButton("salvar", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    salvar();
                    ocultarModal(target);
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
        emailModel.setObject("");
        ativoModel.setObject(cliente.ativo());

        String codigo = "C-" + cliente.id().toString().replace("-", "").substring(0, 4).toUpperCase();
        badgeCodigoModel.setObject(codigo);

        if (cliente instanceof ClientePfDto pf) {
            breadcrumbModel.setObject("atualizar cadastro · pessoa física");
            tipoModel.setObject("pessoa física");
            badgeLabelModel.setObject("CLIENTE");
            badgeNameModel.setObject(pf.nome());
            emailLabelModel.setObject("E-MAIL");
            ativoNumModel.setObject("03");
            nomeModel.setObject("");
            pfCampos.setVisible(true);
            pjCampos.setVisible(false);
        } else {
            ClientePjDto pj = (ClientePjDto) cliente;
            breadcrumbModel.setObject("atualizar cadastro · pessoa jurídica");
            tipoModel.setObject("pessoa jurídica");
            badgeLabelModel.setObject("CNPJ");
            badgeNameModel.setObject(formatarCnpj(pj.cnpj()));
            emailLabelModel.setObject("E-MAIL CORPORATIVO");
            ativoNumModel.setObject("04");
            razaoSocialModel.setObject("");
            inscricaoEstadualModel.setObject("");
            pfCampos.setVisible(false);
            pjCampos.setVisible(true);
        }
    }

    private static String formatarCnpj(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) return cnpj;
        return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "."
             + cnpj.substring(5, 8) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12);
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

    private void ocultarModal(AjaxRequestTarget target) {
        target.appendJavaScript(
            "(function(){var el=document.getElementById('" + getMarkupId() + "');if(el)bootstrap.Modal.getOrCreateInstance(el).hide();})();"
        );
    }

    // Ponto de extensão: subclasses ou páginas que instanciam o modal sobrescrevem para reagir ao salvamento.
    protected void onAtualizado(AjaxRequestTarget target) {}
}
