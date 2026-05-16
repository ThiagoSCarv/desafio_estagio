package com.thiago.desafio_estagio.shared.wicket.components.detalhes;

import com.thiago.desafio_estagio.endereco.application.EnderecoCreateDto;
import com.thiago.desafio_estagio.endereco.application.EnderecoService;
import com.thiago.desafio_estagio.shared.wicket.util.WicketUtil;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.UUID;

public class AdicionarEnderecoModal extends Panel {

    @SpringBean
    private EnderecoService enderecoService;

    private static class EnderecoData implements Serializable {
        String  logradouro        = "";
        String  numero            = "";
        String  cep               = "";
        String  bairro            = "";
        String  telefone          = "";
        String  cidade            = "";
        String  estado            = "";
        Boolean enderecoPrincipal = Boolean.FALSE;
        String  complemento       = "";
    }

    private final EnderecoData formData = new EnderecoData();
    private final FeedbackPanel feedback;

    public AdicionarEnderecoModal(String id, UUID clienteId) {
        super(id);
        setOutputMarkupId(true);

        Form<EnderecoData> form = new Form<>("form", new CompoundPropertyModel<>(formData));
        add(form);

        feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        form.add(new TextField<>("logradouro"));
        form.add(new TextField<>("numero"));
        form.add(new TextField<>("cep"));
        form.add(new TextField<>("bairro"));
        form.add(new TextField<>("telefone"));
        form.add(new TextField<>("cidade"));
        form.add(new TextField<>("estado"));
        RadioGroup<Boolean> principalGroup = new RadioGroup<>("principalGroup", new PropertyModel<>(formData, "enderecoPrincipal"));
        principalGroup.add(new Radio<>("radioSim", Model.of(Boolean.TRUE)));
        principalGroup.add(new Radio<>("radioNao", Model.of(Boolean.FALSE)));
        form.add(principalGroup);
        form.add(new TextField<>("complemento"));

        form.add(new AjaxButton("salvar", form) { // NOSONAR java:S110 — profundidade herdada do Wicket
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    enderecoService.criar(clienteId, new EnderecoCreateDto(
                            formData.logradouro,
                            formData.numero,
                            formData.cep,
                            formData.bairro,
                            emptyToNull(formData.telefone),
                            formData.cidade,
                            formData.estado,
                            formData.enderecoPrincipal,
                            emptyToNull(formData.complemento)
                    ));
                    limpar();
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

    private void limpar() {
        formData.logradouro        = "";
        formData.numero            = "";
        formData.cep               = "";
        formData.bairro            = "";
        formData.telefone          = "";
        formData.cidade            = "";
        formData.estado            = "";
        formData.enderecoPrincipal = Boolean.FALSE;
        formData.complemento       = "";
    }

    private static String emptyToNull(String value) {
        return value != null && !value.isBlank() ? value : null;
    }

    protected void onAdicionado(AjaxRequestTarget target) { // hook para subclasses recarregarem a lista
    }
}
