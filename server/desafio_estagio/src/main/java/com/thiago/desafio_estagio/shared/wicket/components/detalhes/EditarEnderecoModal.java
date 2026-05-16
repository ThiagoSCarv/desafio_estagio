package com.thiago.desafio_estagio.shared.wicket.components.detalhes;

import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
import com.thiago.desafio_estagio.endereco.application.EnderecoService;
import com.thiago.desafio_estagio.endereco.application.EnderecoUpdateDto;
import com.thiago.desafio_estagio.shared.wicket.util.WicketUtil;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.UUID;

public class EditarEnderecoModal extends Panel {

    @SpringBean
    private EnderecoService enderecoService;

    private UUID enderecoId;
    private final Model<String> numeroModel      = Model.of("");
    private final Model<String> telefoneModel    = Model.of("");
    private final Model<String> complementoModel = Model.of("");
    private final Model<Boolean> principalModel  = Model.of(Boolean.FALSE);
    private final FeedbackPanel feedback;

    public EditarEnderecoModal(String id) {
        super(id);
        setOutputMarkupId(true);

        Form<Void> form = new Form<>("form");
        add(form);

        feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        form.add(new TextField<>("numero", numeroModel));
        form.add(new TextField<>("telefone", telefoneModel));
        form.add(new TextField<>("complemento", complementoModel));
        RadioGroup<Boolean> principalGroup = new RadioGroup<>("principalGroup", principalModel);
        principalGroup.add(new Radio<>("radioSim", Model.of(Boolean.TRUE)));
        principalGroup.add(new Radio<>("radioNao", Model.of(Boolean.FALSE)));
        form.add(principalGroup);

        form.add(new AjaxButton("salvar", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    enderecoService.atualizar(enderecoId, new EnderecoUpdateDto(
                            emptyToNull(numeroModel.getObject()),
                            emptyToNull(telefoneModel.getObject()),
                            principalModel.getObject(),
                            emptyToNull(complementoModel.getObject())
                    ));
                    WicketUtil.mostrarToast(target, "Endereço atualizado com sucesso");
                    WicketUtil.ocultarModal(target, EditarEnderecoModal.this);
                    onAtualizado(target);
                } catch (RuntimeException e) {
                    EditarEnderecoModal.this.error(e.getMessage());
                    target.add(feedback);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedback);
            }
        });
    }

    public void setEndereco(EnderecoDto endereco) {
        this.enderecoId = endereco.id();
        numeroModel.setObject(endereco.numero() != null ? endereco.numero() : "");
        telefoneModel.setObject(endereco.telefone() != null ? endereco.telefone() : "");
        complementoModel.setObject(endereco.complemento() != null ? endereco.complemento() : "");
        principalModel.setObject(endereco.enderecoPrincipal());
    }

    private static String emptyToNull(String value) {
        return value != null && !value.isBlank() ? value : null;
    }

    protected void onAtualizado(AjaxRequestTarget target) {}
}
