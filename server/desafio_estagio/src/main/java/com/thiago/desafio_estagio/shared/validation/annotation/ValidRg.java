package com.thiago.desafio_estagio.validation.annotation;

import com.thiago.desafio_estagio.validation.validator.RgValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = RgValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRg {

    String message() default "RG inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
