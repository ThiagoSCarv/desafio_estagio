package com.thiago.desafio_estagio.shared.validation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CpfValidatorTest {

    private CpfValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CpfValidator();
    }

    // null é delegado a @NotBlank/@NotNull — validator não deve rejeitar
    @Test
    void deveAceitarNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void deveAceitarCpfValidoSemMascara() {
        assertThat(validator.isValid("52998224725", null)).isTrue();
    }

    @Test
    void deveAceitarCpfValidoComMascara() {
        assertThat(validator.isValid("529.982.247-25", null)).isTrue();
    }

    @Test
    void deveRejeitarCpfComMenosDe11Digitos() {
        assertThat(validator.isValid("1234567890", null)).isFalse();
    }

    @Test
    void deveRejeitarCpfComMaisDe11Digitos() {
        assertThat(validator.isValid("123456789012", null)).isFalse();
    }

    @Test
    void deveRejeitarCpfComTodosDigitosIguais() {
        assertThat(validator.isValid("11111111111", null)).isFalse();
        assertThat(validator.isValid("000.000.000-00", null)).isFalse();
        assertThat(validator.isValid("99999999999", null)).isFalse();
    }

    @Test
    void deveRejeitarCpfComPrimeiroDigitoVerificadorErrado() {
        // CPF válido é 12345678909; posição 9 trocada de 0 para 1
        assertThat(validator.isValid("12345678919", null)).isFalse();
    }

    @Test
    void deveRejeitarCpfComSegundoDigitoVerificadorErrado() {
        // CPF válido é 12345678909; posição 10 trocada de 9 para 0
        assertThat(validator.isValid("12345678900", null)).isFalse();
    }

    @Test
    void deveAceitarCpfComZerosNaoRepeticao() {
        // CPF válido que contém zeros mas não todos iguais
        assertThat(validator.isValid("12345678909", null)).isTrue();
    }
}
