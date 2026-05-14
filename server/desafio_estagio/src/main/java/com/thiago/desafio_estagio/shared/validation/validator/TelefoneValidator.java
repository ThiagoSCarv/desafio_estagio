package com.thiago.desafio_estagio.shared.validation.validator;

import com.thiago.desafio_estagio.shared.validation.annotation.ValidTelefone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String telefone = value.replaceAll("\\D", "");

        if (telefone.length() != 10 && telefone.length() != 11) return false;

        int ddd = Integer.parseInt(telefone.substring(0, 2));
        if (ddd < 11 || ddd > 99) return false;

        // celular: terceiro dígito deve ser 9
        return telefone.length() != 11 || telefone.charAt(2) == '9';
    }
}
