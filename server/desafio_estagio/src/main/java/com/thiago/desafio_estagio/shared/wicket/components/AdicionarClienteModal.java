package com.thiago.desafio_estagio.shared.wicket.components;

import com.thiago.desafio_estagio.cliente.application.ClientePfCreateDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfService;
import com.thiago.desafio_estagio.cliente.application.ClientePjCreateDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjService;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.endereco.application.EnderecoCreateDto;
import com.thiago.desafio_estagio.shared.validation.validator.CnpjValidator;
import com.thiago.desafio_estagio.shared.validation.validator.CpfValidator;
import com.thiago.desafio_estagio.shared.validation.validator.RgValidator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    private final List<EnderecoPanel.EnderecoEntry> enderecos = new ArrayList<>();

    private final WebMarkupContainer pfCampos;
    private final WebMarkupContainer pjCampos;
    private final WebMarkupContainer enderecoContainer;
    private final FeedbackPanel feedback;

    public AdicionarClienteModal(String id) {
        super(id);
        setOutputMarkupId(true);

        enderecos.add(new EnderecoPanel.EnderecoEntry());

        Form<Void> form = new Form<>("form");
        add(form);

        feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        pfCampos = new WebMarkupContainer("pfCampos");
        pfCampos.setOutputMarkupPlaceholderTag(true);
        pfCampos.add(new TextField<>("nome", nomeModel));

        TextField<String> cpfField = new TextField<>("cpf", cpfModel);
        cpfField.add((IValidator<String>) validatable -> {
            String v = validatable.getValue();
            if (v != null && !v.isBlank() && !new CpfValidator().isValid(v, null))
                validatable.error(new ValidationError("CPF inválido"));
        });
        pfCampos.add(cpfField);

        TextField<String> rgField = new TextField<>("rg", rgModel);
        rgField.add((IValidator<String>) validatable -> {
            String v = validatable.getValue();
            if (v != null && !v.isBlank() && !new RgValidator().isValid(v, null))
                validatable.error(new ValidationError("RG inválido"));
        });
        pfCampos.add(rgField);

        pfCampos.add(new TextField<>("dataNascimento", dataNascimentoModel));

        pjCampos = new WebMarkupContainer("pjCampos");
        pjCampos.setOutputMarkupPlaceholderTag(true);
        pjCampos.setVisible(false);

        TextField<String> cnpjField = new TextField<>("cnpj", cnpjModel);
        cnpjField.add((IValidator<String>) validatable -> {
            String v = validatable.getValue();
            if (v != null && !v.isBlank() && !new CnpjValidator().isValid(v, null))
                validatable.error(new ValidationError("CNPJ inválido"));
        });
        pjCampos.add(cnpjField);
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

        enderecoContainer = new WebMarkupContainer("enderecoContainer");
        enderecoContainer.setOutputMarkupId(true);

        ListView<EnderecoPanel.EnderecoEntry> enderecoListView = new ListView<EnderecoPanel.EnderecoEntry>("enderecoList", enderecos) {
            @Override
            protected void populateItem(ListItem<EnderecoPanel.EnderecoEntry> item) {
                item.add(new EnderecoPanel("enderecoPanel", item.getModel()) {
                    @Override
                    protected boolean canRemove() {
                        return enderecos.size() > 1;
                    }

                    @Override
                    protected void onRemover(AjaxRequestTarget target) {
                        removerEndereco(item.getModelObject(), target);
                    }
                });
            }
        };
        enderecoListView.setReuseItems(false);
        enderecoContainer.add(enderecoListView);
        form.add(enderecoContainer);

        form.add(new AjaxSubmitLink("btnAdicionarEndereco", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                adicionarEndereco(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                adicionarEndereco(target);
            }
        });

        form.add(new AjaxButton("salvar", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    criar();
                    mostrarToast(target, "Cadastro realizado com sucesso");
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

    private void adicionarEndereco(AjaxRequestTarget target) {
        enderecos.add(new EnderecoPanel.EnderecoEntry());
        target.add(enderecoContainer);
    }

    private void removerEndereco(EnderecoPanel.EnderecoEntry entry, AjaxRequestTarget target) {
        enderecos.remove(entry);
        target.add(enderecoContainer);
    }

    private void criar() {
        List<EnderecoCreateDto> enderecosDtos = enderecos.stream()
                .map(e -> new EnderecoCreateDto(
                        e.logradouro,
                        e.numero,
                        e.cep,
                        e.bairro,
                        emptyToNull(e.telefone),
                        e.cidade,
                        e.estado,
                        e.enderecoPrincipal,
                        emptyToNull(e.complemento)
                ))
                .toList();

        if (tipoSelecionado == TipoPessoa.FISICA) {
            clientePfService.criar(new ClientePfCreateDto(
                    emailModel.getObject(),
                    nomeModel.getObject(),
                    cpfModel.getObject(),
                    rgModel.getObject(),
                    parseDate(dataNascimentoModel.getObject()),
                    enderecosDtos
            ));
        } else {
            clientePjService.criar(new ClientePjCreateDto(
                    emailModel.getObject(),
                    cnpjModel.getObject(),
                    razaoSocialModel.getObject(),
                    inscricaoEstadualModel.getObject(),
                    parseDate(dataCriacaoModel.getObject()),
                    enderecosDtos
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

    private String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
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

        enderecos.clear();
        enderecos.add(new EnderecoPanel.EnderecoEntry());
        target.add(enderecoContainer);
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

    private void mostrarToast(AjaxRequestTarget target, String mensagem) {
        target.appendJavaScript(
            "(function(){" +
            "var wrap=document.createElement('div');" +
            "wrap.style.cssText='position:fixed;bottom:1.5rem;right:1.5rem;z-index:11000;';" +
            "var t=document.createElement('div');" +
            "t.className='toast align-items-center border-0';" +
            "t.style.cssText='background:var(--erp-success);color:var(--erp-bg);';" +
            "t.setAttribute('role','alert');" +
            "t.innerHTML='<div class=\"d-flex\"><div class=\"toast-body fw-medium\">" + mensagem + "</div>" +
            "<button type=\"button\" class=\"btn-close me-2 m-auto\" data-bs-dismiss=\"toast\"></button></div>';" +
            "wrap.appendChild(t);document.body.appendChild(wrap);" +
            "var bsT=new bootstrap.Toast(t,{delay:4000});bsT.show();" +
            "t.addEventListener('hidden.bs.toast',function(){wrap.remove();});" +
            "})();"
        );
    }

    protected void onAdicionado(AjaxRequestTarget target) {}

}
