package com.thiago.desafio_estagio.shared.validation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RgValidatorTest {

    private RgValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RgValidator();
    }

    @Test
    void deveAceitarNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void deveAceitarRgNumericoValido() {
        assertThat(validator.isValid("1234567", null)).isTrue();
    }

    @Test
    void deveAceitarRgComDigitoXNoFinal() {
        // dígito verificador X é usado por alguns estados
        assertThat(validator.isValid("123456X", null)).isTrue();
    }

    @Test
    void deveAceitarRgComMascaraDeHifenEPonto() {
        // "12.345.678-9" → remove máscara → "123456789" (9 chars)
        assertThat(validator.isValid("12.345.678-9", null)).isTrue();
    }

    @Test
    void deveAceitarRgNoLimiteInferior() {
        // 5 caracteres após remover máscara
        assertThat(validator.isValid("12345", null)).isTrue();
    }

    @Test
    void deveAceitarRgNoLimiteSuperior() {
        // 14 caracteres após remover máscara
        assertThat(validator.isValid("12345678901234", null)).isTrue();
    }

    @Test
    void deveRejeitarRgComMenosDe5Caracteres() {
        assertThat(validator.isValid("1234", null)).isFalse();
    }

    @Test
    void deveRejeitarRgComMaisDe14Caracteres() {
        assertThat(validator.isValid("123456789012345", null)).isFalse();
    }

    @Test
    void deveRejeitarRgComLetrasInvalidas() {
        // validator só permite [0-9X]; letras como 'A' são inválidas
        assertThat(validator.isValid("1234A67", null)).isFalse();
    }

    @Test
    void deveRejeitarRgComTodosDigitosIguais() {
        assertThat(validator.isValid("1111111", null)).isFalse();
        assertThat(validator.isValid("XXXXXXX", null)).isFalse();
    }
}
