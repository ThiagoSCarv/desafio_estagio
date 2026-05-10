package com.thiago.desafio_estagio.shared.validation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TelefoneValidatorTest {

    private TelefoneValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TelefoneValidator();
    }

    @Test
    void deveAceitarNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void deveAceitarTelefoneFixoValido() {
        // DDD 11, fixo: 10 dígitos
        assertThat(validator.isValid("1133334444", null)).isTrue();
    }

    @Test
    void deveAceitarCelularValido() {
        // DDD 11, celular: 11 dígitos, terceiro dígito = 9
        assertThat(validator.isValid("11987654321", null)).isTrue();
    }

    @Test
    void deveAceitarTelefoneComMascara() {
        assertThat(validator.isValid("(11) 98765-4321", null)).isTrue();
        assertThat(validator.isValid("(11) 3333-4444", null)).isTrue();
    }

    @Test
    void deveRejeitarComMenosDe10Digitos() {
        assertThat(validator.isValid("119876543", null)).isFalse();
    }

    @Test
    void deveRejeitarComMaisDe11Digitos() {
        assertThat(validator.isValid("119876543210", null)).isFalse();
    }

    @Test
    void deveRejeitarDddMenorQue11() {
        // DDD 10 é inválido no Brasil
        assertThat(validator.isValid("1033334444", null)).isFalse();
    }

    @Test
    void deveRejeitarDddZero() {
        assertThat(validator.isValid("0033334444", null)).isFalse();
    }

    @Test
    void deveRejeitarCelularComTerceiroDigitoDiferenteDe9() {
        // 11 dígitos mas terceiro dígito é 8, não 9
        assertThat(validator.isValid("11887654321", null)).isFalse();
    }

    @Test
    void deveAceitarDddNoLimiteSuperior() {
        // DDD 99 (limite máximo aceito pelo validator)
        assertThat(validator.isValid("9933334444", null)).isTrue();
    }
}
