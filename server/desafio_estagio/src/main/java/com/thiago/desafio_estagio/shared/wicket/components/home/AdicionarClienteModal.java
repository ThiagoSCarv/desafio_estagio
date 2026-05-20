package com.thiago.desafio_estagio.shared.wicket.components.home;

import com.thiago.desafio_estagio.cliente.application.ClientePfCreateDto;
import com.thiago.desafio_estagio.cliente.application.ClientePfService;
import com.thiago.desafio_estagio.cliente.application.ClientePjCreateDto;
import com.thiago.desafio_estagio.cliente.application.ClientePjService;
import com.thiago.desafio_estagio.cliente.domain.TipoPessoa;
import com.thiago.desafio_estagio.endereco.application.EnderecoCreateDto;
import com.thiago.desafio_estagio.shared.validation.validator.CnpjValidator;
import com.thiago.desafio_estagio.shared.validation.validator.CpfValidator;
import com.thiago.desafio_estagio.shared.validation.validator.RgValidator;
import com.thiago.desafio_estagio.shared.utils.JsUtils;
import com.thiago.desafio_estagio.shared.wicket.util.WicketUtil;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
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

    private static final String DATA_MASK = "data-mask";

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
    private final Form<Void> form;

    public AdicionarClienteModal(String id) {
        super(id);
        setOutputMarkupId(true);

        enderecos.add(new EnderecoPanel.EnderecoEntry());

        form = new Form<>("form");
        form.setOutputMarkupId(true);
        add(form);

        feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        pfCampos = new WebMarkupContainer("pfCampos");
        pfCampos.setOutputMarkupPlaceholderTag(true);
        pfCampos.add(new TextField<>("nome", nomeModel));

        TextField<String> cpfField = new TextField<>("cpf", cpfModel);
        cpfField.add(AttributeModifier.replace(DATA_MASK, "cpf"));
        cpfField.add((IValidator<String>) validatable -> {
            String v = validatable.getValue();
            if (v != null && !v.isBlank() && !new CpfValidator().isValid(v, null))
                validatable.error(new ValidationError("CPF inválido"));
        });
        pfCampos.add(cpfField);

        TextField<String> rgField = new TextField<>("rg", rgModel);
        rgField.add(AttributeModifier.replace(DATA_MASK, "rg"));
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
        cnpjField.add(AttributeModifier.replace(DATA_MASK, "cnpj"));
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

        AjaxButton cancelar = new AjaxButton("cancelar") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                limpar(target);
                WicketUtil.ocultarModal(target, AdicionarClienteModal.this);
            }
        };
        // Pula validação — cancelar não deve falhar por campos obrigatórios em branco.
        cancelar.setDefaultFormProcessing(false);
        form.add(cancelar);

        form.add(new AjaxButton("salvar", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    criar();
                    WicketUtil.mostrarToast(target, "Cadastro realizado com sucesso");
                    limpar(target);
                    WicketUtil.ocultarModal(target, AdicionarClienteModal.this);
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
        target.appendJavaScript(WicketUtil.INIT_MASKS);
    }

    private void removerEndereco(EnderecoPanel.EnderecoEntry entry, AjaxRequestTarget target) {
        enderecos.remove(entry);
        target.add(enderecoContainer);
        target.appendJavaScript(WicketUtil.INIT_MASKS);
    }

    private void validarEnderecos() {
        for (int i = 0; i < enderecos.size(); i++) {
            String prefixo = enderecos.size() > 1 ? "Endereço " + (i + 1) + ": " : "";
            validarEntry(enderecos.get(i), prefixo);
        }
    }

    private static void validarEntry(EnderecoPanel.EnderecoEntry e, String prefixo) {
        WicketUtil.exigir(e.cep,        prefixo + "CEP é obrigatório.");
        WicketUtil.exigir(e.logradouro, prefixo + "Logradouro é obrigatório.");
        if (e.numero == null) throw new IllegalArgumentException(prefixo + "Número é obrigatório.");
        WicketUtil.exigir(e.bairro,     prefixo + "Bairro é obrigatório.");
        WicketUtil.exigir(e.cidade,     prefixo + "Cidade é obrigatória.");
        WicketUtil.exigir(e.estado,     prefixo + "Estado é obrigatório.");
    }

    private void criar() {
        validarEnderecos();
        WicketUtil.exigir(emailModel.getObject(), "E-mail é obrigatório.");

        List<EnderecoCreateDto> enderecosDtos = mapEnderecos();

        if (tipoSelecionado == TipoPessoa.FISICA) {
            WicketUtil.exigir(nomeModel.getObject(), "Nome é obrigatório.");
            WicketUtil.exigir(cpfModel.getObject(), "CPF é obrigatório.");
            WicketUtil.exigir(rgModel.getObject(), "RG é obrigatório.");
            clientePfService.criar(new ClientePfCreateDto(
                    emailModel.getObject(),
                    nomeModel.getObject(),
                    cpfModel.getObject(),
                    rgModel.getObject(),
                    parseDate(dataNascimentoModel.getObject(), "Data de nascimento"),
                    enderecosDtos
            ));
        } else {
            WicketUtil.exigir(cnpjModel.getObject(), "CNPJ é obrigatório.");
            WicketUtil.exigir(razaoSocialModel.getObject(), "Razão social é obrigatória.");
            WicketUtil.exigir(inscricaoEstadualModel.getObject(), "Inscrição estadual é obrigatória.");
            clientePjService.criar(new ClientePjCreateDto(
                    emailModel.getObject(),
                    cnpjModel.getObject(),
                    razaoSocialModel.getObject(),
                    inscricaoEstadualModel.getObject(),
                    parseDate(dataCriacaoModel.getObject(), "Data de fundação"),
                    enderecosDtos
            ));
        }
    }

    private List<EnderecoCreateDto> mapEnderecos() {
        return enderecos.stream()
                .map(e -> new EnderecoCreateDto(
                        e.cep,
                        e.logradouro,
                        e.numero,
                        e.bairro,
                        e.cidade,
                        e.estado,
                        WicketUtil.emptyToNull(e.telefone),
                        e.enderecoPrincipal,
                        WicketUtil.emptyToNull(e.complemento)
                ))
                .toList();
    }

    private LocalDate parseDate(String value, String nomeCampo) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(nomeCampo + " é obrigatória.");
        }
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            throw new IllegalArgumentException(nomeCampo + " inválida. Use dd/mm/aaaa.");
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

        enderecos.clear();
        enderecos.add(new EnderecoPanel.EnderecoEntry());

        getFeedbackMessages().clear();
        WicketUtil.limparForm(form, target);
        target.appendJavaScript(WicketUtil.INIT_MASKS);
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

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(JsUtils.MASKS));
    }

    protected void onAdicionado(AjaxRequestTarget target) {}

}
