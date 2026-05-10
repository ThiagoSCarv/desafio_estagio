package com.thiago.desafio_estagio.shared.validation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CepValidatorTest {

    private CepValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CepValidator();
    }

    @Test
    void deveAceitarNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void deveAceitarCepSemMascara() {
        assertThat(validator.isValid("01310100", null)).isTrue();
    }

    @Test
    void deveAceitarCepComMascara() {
        assertThat(validator.isValid("01310-100", null)).isTrue();
    }

    @Test
    void deveRejeitarCepComMenosDe8Digitos() {
        assertThat(validator.isValid("0131010", null)).isFalse();
    }

    @Test
    void deveRejeitarCepComMaisDe8Digitos() {
        assertThat(validator.isValid("013101000", null)).isFalse();
    }

    @Test
    void deveRejeitarCepVazio() {
        assertThat(validator.isValid("", null)).isFalse();
    }
}
