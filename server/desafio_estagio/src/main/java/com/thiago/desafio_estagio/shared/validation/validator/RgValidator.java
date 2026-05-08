package com.thiago.desafio_estagio.validation.validator;

import com.thiago.desafio_estagio.validation.annotation.ValidRg;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RgValidator implements ConstraintValidator<ValidRg, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        // remove máscara: pontos, traços e espaços
        String rg = value.replaceAll("[.\\-\\s]", "").toUpperCase();

        // entre 5 e 14 caracteres alfanuméricos (cobre todos os estados)
        if (rg.length() < 5 || rg.length() > 14) return false;

        // permite apenas dígitos e X (dígito verificador em alguns estados)
        if (!rg.matches("[0-9X]+")) return false;

        // rejeita sequências repetidas (ex: 111111111)
        if (rg.chars().distinct().count() == 1) return false;

        return true;
    }
}
