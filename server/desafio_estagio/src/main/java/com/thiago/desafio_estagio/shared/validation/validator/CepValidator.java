package com.thiago.desafio_estagio.shared.validation.validator;

import com.thiago.desafio_estagio.shared.validation.annotation.ValidCep;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CepValidator implements ConstraintValidator<ValidCep, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String cep = value.replaceAll("[^0-9]", "");

        return cep.length() == 8;
    }
}
