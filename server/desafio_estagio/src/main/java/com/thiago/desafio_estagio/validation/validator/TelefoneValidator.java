package com.thiago.desafio_estagio.validation.validator;

import com.thiago.desafio_estagio.validation.annotation.ValidTelefone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String telefone = value.replaceAll("[^0-9]", "");

        if (telefone.length() != 10 && telefone.length() != 11) return false;

        int ddd = Integer.parseInt(telefone.substring(0, 2));
        if (ddd < 11 || ddd > 99) return false;

        // celular: 11 dígitos e terceiro dígito deve ser 9
        if (telefone.length() == 11 && telefone.charAt(2) != '9') return false;

        return true;
    }
}
