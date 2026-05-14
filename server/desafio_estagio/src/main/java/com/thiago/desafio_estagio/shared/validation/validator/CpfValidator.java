package com.thiago.desafio_estagio.shared.validation.validator;

import com.thiago.desafio_estagio.shared.validation.annotation.ValidCpf;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<ValidCpf, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String cpf = value.replaceAll("\\D", "");

        if (cpf.length() != 11) return false;

        if (cpf.chars().distinct().count() == 1) return false;

        return digitoValido(cpf, 9) && digitoValido(cpf, 10);
    }

    private boolean digitoValido(String cpf, int posicao) {
        int soma = 0;
        for (int i = 0; i < posicao; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (posicao + 1 - i);
        }
        int resto = (soma * 10) % 11;
        int digito = (resto == 10 || resto == 11) ? 0 : resto;
        return digito == Character.getNumericValue(cpf.charAt(posicao));
    }
}
