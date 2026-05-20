package com.thiago.desafio_estagio.shared.validation.annotation;

import com.thiago.desafio_estagio.shared.validation.validator.RgValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Valida RG alfanumérico (letras, dígitos, pontos, traços e espaços). Aceita nulo.
@Documented
@Constraint(validatedBy = RgValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRg {

    String message() default "RG inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
