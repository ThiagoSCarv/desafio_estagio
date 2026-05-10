package com.thiago.desafio_estagio.shared.validation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CnpjValidatorTest {

    private CnpjValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CnpjValidator();
    }

    @Test
    void deveAceitarNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void deveAceitarCnpjValidoSemMascara() {
        assertThat(validator.isValid("11222333000181", null)).isTrue();
    }

    @Test
    void deveAceitarCnpjValidoComMascara() {
        assertThat(validator.isValid("11.222.333/0001-81", null)).isTrue();
    }

    @Test
    void deveRejeitarCnpjComMenosDe14Digitos() {
        assertThat(validator.isValid("1122233300018", null)).isFalse();
    }

    @Test
    void deveRejeitarCnpjComMaisDe14Digitos() {
        assertThat(validator.isValid("112223330001810", null)).isFalse();
    }

    @Test
    void deveRejeitarCnpjComTodosDigitosIguais() {
        assertThat(validator.isValid("11111111111111", null)).isFalse();
        assertThat(validator.isValid("00000000000000", null)).isFalse();
        assertThat(validator.isValid("99.999.999/9999-99", null)).isFalse();
    }

    @Test
    void deveRejeitarCnpjComPrimeiroDigitoVerificadorErrado() {
        // CNPJ válido é 11222333000181; posição 12 (dígito '8') trocada para '0'
        assertThat(validator.isValid("11222333000101", null)).isFalse();
    }

    @Test
    void deveRejeitarCnpjComSegundoDigitoVerificadorErrado() {
        // CNPJ válido é 11222333000181; posição 13 (dígito '1') trocada para '0'
        assertThat(validator.isValid("11222333000180", null)).isFalse();
    }
}
