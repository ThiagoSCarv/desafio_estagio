package com.thiago.desafio_estagio.shared.wicket.util;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class WicketUtil {

    // Wicket substitui o HTML do painel antes de executar qualquer JS do Ajax.
    // Esse trecho limpa o estado do Bootstrap diretamente no DOM após a substituição.
    public static final String MODAL_CLEANUP_JS =
        "document.querySelectorAll('.modal-backdrop').forEach(function(b){b.remove();});" +
        "document.body.classList.remove('modal-open');" +
        "document.body.style.removeProperty('overflow');" +
        "document.body.style.removeProperty('padding-right');";

    private WicketUtil() {}

    public static void mostrarToast(AjaxRequestTarget target, String mensagem) {
        target.appendJavaScript(
            "(function(){" +
            "var wrap=document.createElement('div');" +
            "wrap.style.cssText='position:fixed;bottom:1.5rem;right:1.5rem;z-index:11000;';" +
            "var t=document.createElement('div');" +
            "t.className='toast align-items-center border-0';" +
            "t.style.cssText='background:var(--erp-success);color:var(--erp-bg);';" +
            "t.setAttribute('role','alert');" +
            "t.setAttribute('aria-atomic','true');" +
            "t.innerHTML='<div class=\"d-flex\"><div class=\"toast-body fw-medium\">" + mensagem + "</div>" +
            "<button type=\"button\" class=\"btn-close me-2 m-auto\" data-bs-dismiss=\"toast\" aria-label=\"Fechar\"></button></div>';" +
            "wrap.appendChild(t);document.body.appendChild(wrap);" +
            "var bsT=new bootstrap.Toast(t,{delay:4000});bsT.show();" +
            "t.addEventListener('hidden.bs.toast',function(){wrap.remove();});" +
            "})();"
        );
    }

    public static void ocultarModal(AjaxRequestTarget target, Component modal) {
        target.appendJavaScript(
            "(function(){var el=document.getElementById('" + modal.getMarkupId() + "');" +
            "if(el)bootstrap.Modal.getOrCreateInstance(el).hide();})();"
        );
    }
}
