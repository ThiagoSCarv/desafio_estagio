package com.thiago.desafio_estagio.shared.validation.validator;

import br.com.caelum.stella.validation.InvalidStateException;
import com.thiago.desafio_estagio.shared.validation.annotation.ValidCnpj;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// Delega o algoritmo de validação para Caelum Stella, mantendo a anotação Jakarta.
// O módulo bean-validation oficial do Stella é javax — por isso embrulhamos o core aqui.
public class CnpjValidator implements ConstraintValidator<ValidCnpj, String> {

    private final br.com.caelum.stella.validation.CNPJValidator delegate =
            new br.com.caelum.stella.validation.CNPJValidator();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        try {
            delegate.assertValid(value.replaceAll("\\D", ""));
            return true;
        } catch (InvalidStateException e) {
            return false;
        }
    }
}
