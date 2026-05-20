package com.thiago.desafio_estagio.shared.utils;

import org.apache.wicket.request.resource.PackageResourceReference;

// Referências centralizadas a recursos JS usados nos componentes Wicket.
public class JsUtils {

    // masks.js aplica máscaras de CPF, CNPJ, CEP e telefone nos campos de formulário.
    public static final PackageResourceReference MASKS =
            new PackageResourceReference(JsUtils.class, "masks.js");

    private JsUtils() {}
}
