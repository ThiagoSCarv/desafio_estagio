package com.thiago.desafio_estagio.shared.utils;

import org.apache.wicket.request.resource.PackageResourceReference;

public class JsUtils {

    public static final PackageResourceReference MASKS =
            new PackageResourceReference(JsUtils.class, "masks.js");

    private JsUtils() {}
}
