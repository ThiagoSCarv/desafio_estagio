package com.thiago.desafio_estagio.shared.wicket.components.detalhes;

import com.thiago.desafio_estagio.endereco.application.EnderecoDto;
import com.thiago.desafio_estagio.endereco.application.EnderecoService;
import com.thiago.desafio_estagio.endereco.application.EnderecoUpdateDto;
import com.thiago.desafio_estagio.shared.wicket.util.WicketUtil;
import jakarta.validation.ConstraintViolationException;
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
    private final Model<Integer> numeroModel     = Model.of((Integer) null);
    private final Model<String> telefoneModel    = Model.of("");
    private final Model<String> complementoModel = Model.of("");
    private final Model<Boolean> principalModel  = Model.of(Boolean.FALSE);
    private final FeedbackPanel feedback;
    private final Form<Void> form;

    public EditarEnderecoModal(String id) {
        super(id);
        setOutputMarkupId(true);

        form = new Form<>("form");
        form.setOutputMarkupId(true);
        add(form);

        feedback = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        form.add(new TextField<>("numero", numeroModel, Integer.class));
        form.add(new TextField<>("telefone", telefoneModel));
        form.add(new TextField<>("complemento", complementoModel));
        RadioGroup<Boolean> principalGroup = new RadioGroup<>("principalGroup", principalModel);
        principalGroup.add(new Radio<>("radioSim", Model.of(Boolean.TRUE)));
        principalGroup.add(new Radio<>("radioNao", Model.of(Boolean.FALSE)));
        form.add(principalGroup);

        AjaxButton cancelar = new AjaxButton("cancelar") { // NOSONAR java:S110 — profundidade herdada do Wicket
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                limpar(target);
                WicketUtil.ocultarModal(target, EditarEnderecoModal.this);
            }
        };
        // Pula validação — cancelar não deve falhar por campos obrigatórios em branco.
        cancelar.setDefaultFormProcessing(false);
        form.add(cancelar);

        form.add(new AjaxButton("salvar", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    enderecoService.atualizar(enderecoId, new EnderecoUpdateDto(
                            numeroModel.getObject(),
                            WicketUtil.emptyToNull(telefoneModel.getObject()),
                            principalModel.getObject(),
                            WicketUtil.emptyToNull(complementoModel.getObject())
                    ));
                    WicketUtil.mostrarToast(target, "Endereço atualizado com sucesso");
                    WicketUtil.ocultarModal(target, EditarEnderecoModal.this);
                    onAtualizado(target);
                } catch (ConstraintViolationException e) {
                    e.getConstraintViolations().forEach(v ->
                            EditarEnderecoModal.this.error(v.getMessage()));
                    target.add(feedback);
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

    // Reseta o estado do formulário (Models + cache de input bruto dos componentes) e marca o form para re-render via Ajax.
    private void limpar(AjaxRequestTarget target) {
        enderecoId = null;
        numeroModel.setObject(null);
        telefoneModel.setObject("");
        complementoModel.setObject("");
        principalModel.setObject(Boolean.FALSE);
        WicketUtil.limparForm(form, target);
    }

    public void setEndereco(EnderecoDto endereco) {
        this.enderecoId = endereco.id();
        numeroModel.setObject(endereco.numero());
        telefoneModel.setObject(WicketUtil.emptyToString(endereco.telefone()));
        complementoModel.setObject(WicketUtil.emptyToString(endereco.complemento()));
        principalModel.setObject(endereco.enderecoPrincipal());
    }

    protected void onAtualizado(AjaxRequestTarget target) {}
}
