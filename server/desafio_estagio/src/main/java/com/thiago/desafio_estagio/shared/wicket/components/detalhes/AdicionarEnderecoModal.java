package com.thiago.desafio_estagio.shared.wicket.components.detalhes;

import com.thiago.desafio_estagio.endereco.application.EnderecoCreateDto;
import com.thiago.desafio_estagio.endereco.application.EnderecoService;
import com.thiago.desafio_estagio.endereco.application.ViaCepClient;
import com.thiago.desafio_estagio.endereco.application.ViaCepResponseDto;
import com.thiago.desafio_estagio.shared.wicket.util.WicketUtil;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.UUID;

public class AdicionarEnderecoModal extends Panel {

    @SpringBean
    private EnderecoService enderecoService;

    @SpringBean
    private ViaCepClient viaCepClient;

    private static class EnderecoData implements Serializable {
        String  cep               = "";
        String  logradouro        = "";
        String  numero            = "";
        String  bairro            = "";
        String  cidade            = "";
        String  estado            = "";
        String  telefone          = "";
        Boolean enderecoPrincipal = Boolean.FALSE;
        String  complemento       = "";
    }

    private final EnderecoData formData = new EnderecoData();
    private final FeedbackPanel feedback;
    private final Form<EnderecoData> form;

    public AdicionarEnderecoModal(String id, UUID clienteId) {
        super(id);
        setOutputMarkupId(true);

        form = new Form<>("form", new CompoundPropertyModel<>(formData));
        form.setOutputMarkupId(true);
        add(form);

        feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        // Campos preenchidos automaticamente via ViaCEP — ficam editáveis para correção manual
        TextField<String> logradouroField = new TextField<>("logradouro");
        logradouroField.setOutputMarkupId(true);

        TextField<String> bairroField = new TextField<>("bairro");
        bairroField.setOutputMarkupId(true);

        TextField<String> cidadeField = new TextField<>("cidade");
        cidadeField.setOutputMarkupId(true);

        TextField<String> estadoField = new TextField<>("estado");
        estadoField.setOutputMarkupId(true);

        TextField<String> cepField = new TextField<>("cep");
        cepField.add(new AjaxFormComponentUpdatingBehavior("change") { // NOSONAR java:S110 — profundidade herdada do Wicket
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String cep = formData.cep != null ? formData.cep : "";
                if (cep.replaceAll("\\D", "").length() != 8) {
                    return;
                }
                try {
                    ViaCepResponseDto viaCep = viaCepClient.buscarPorCep(cep);
                    formData.logradouro = nvl(viaCep.logradouro());
                    formData.bairro     = nvl(viaCep.bairro());
                    formData.cidade     = nvl(viaCep.localidade());
                    formData.estado     = nvl(viaCep.uf());
                } catch (RuntimeException e) {
                    AdicionarEnderecoModal.this.error("CEP não encontrado. Preencha os campos manualmente.");
                    target.add(feedback);
                }
                target.add(logradouroField, bairroField, cidadeField, estadoField);
            }

            @Override
            protected void onError(AjaxRequestTarget target, RuntimeException e) {
                // CEP ainda incompleto ou inválido — não exibir erro enquanto o usuário digita
            }
        });

        form.add(cepField);
        form.add(logradouroField);
        form.add(new TextField<>("numero"));
        form.add(bairroField);
        form.add(cidadeField);
        form.add(estadoField);
        form.add(new TextField<>("telefone"));

        RadioGroup<Boolean> principalGroup = new RadioGroup<>("principalGroup", new PropertyModel<>(formData, "enderecoPrincipal"));
        principalGroup.add(new Radio<>("radioSim", Model.of(Boolean.TRUE)));
        principalGroup.add(new Radio<>("radioNao", Model.of(Boolean.FALSE)));
        form.add(principalGroup);
        form.add(new TextField<>("complemento"));

        AjaxButton cancelar = new AjaxButton("cancelar") { // NOSONAR java:S110 — profundidade herdada do Wicket
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                limpar(target);
                WicketUtil.ocultarModal(target, AdicionarEnderecoModal.this);
            }
        };
        // Pula validação — cancelar não deve falhar por campos obrigatórios em branco.
        cancelar.setDefaultFormProcessing(false);
        form.add(cancelar);

        form.add(new AjaxButton("salvar", form) { // NOSONAR java:S110 — profundidade herdada do Wicket
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    validarFormData();
                    enderecoService.criar(clienteId, new EnderecoCreateDto(
                            formData.cep,
                            formData.logradouro,
                            formData.numero,
                            formData.bairro,
                            formData.cidade,
                            formData.estado,
                            emptyToNull(formData.telefone),
                            formData.enderecoPrincipal,
                            emptyToNull(formData.complemento)
                    ));
                    limpar(target);
                    WicketUtil.mostrarToast(target, "Endereço adicionado com sucesso");
                    WicketUtil.ocultarModal(target, AdicionarEnderecoModal.this);
                    onAdicionado(target);
                } catch (RuntimeException e) {
                    AdicionarEnderecoModal.this.error(e.getMessage());
                    target.add(feedback);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedback);
            }
        });
    }

    private void validarFormData() {
        exigir(formData.cep,        "CEP é obrigatório.");
        exigir(formData.logradouro, "Logradouro é obrigatório.");
        exigir(formData.numero,     "Número é obrigatório.");
        exigir(formData.bairro,     "Bairro é obrigatório.");
        exigir(formData.cidade,     "Cidade é obrigatória.");
        exigir(formData.estado,     "Estado é obrigatório.");
    }

    private static void exigir(String value, String mensagem) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(mensagem);
    }

    // Reseta o estado do formulário (POJO + cache de input bruto dos componentes) e marca o form para re-render via Ajax.
    private void limpar(AjaxRequestTarget target) {
        formData.cep               = "";
        formData.logradouro        = "";
        formData.numero            = "";
        formData.bairro            = "";
        formData.cidade            = "";
        formData.estado            = "";
        formData.telefone          = "";
        formData.enderecoPrincipal = Boolean.FALSE;
        formData.complemento       = "";
        form.visitFormComponents((fc, visit) -> fc.clearInput());
        target.add(form);
    }

    private static String emptyToNull(String value) {
        return value != null && !value.isBlank() ? value : null;
    }

    private static String nvl(String value) {
        return value != null ? value : "";
    }

    protected void onAdicionado(AjaxRequestTarget target) { // hook para subclasses recarregarem a lista
    }
}
