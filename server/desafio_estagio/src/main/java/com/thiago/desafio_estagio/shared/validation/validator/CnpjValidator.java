package com.thiago.desafio_estagio.shared.validation.validator;

import com.thiago.desafio_estagio.shared.validation.annotation.ValidCnpj;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CnpjValidator implements ConstraintValidator<ValidCnpj, String> {

    private static final int[] PESOS_PRIMEIRO = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_SEGUNDO  = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String cnpj = value.replaceAll("\\D", "");

        if (cnpj.length() != 14) return false;

        if (cnpj.chars().distinct().count() == 1) return false;

        return digitoValido(cnpj, PESOS_PRIMEIRO, 12) && digitoValido(cnpj, PESOS_SEGUNDO, 13);
    }

    private boolean digitoValido(String cnpj, int[] pesos, int posicao) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        int digito = resto < 2 ? 0 : 11 - resto;
        return digito == Character.getNumericValue(cnpj.charAt(posicao));
    }
}
