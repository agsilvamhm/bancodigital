package com.agsilvamhm.bancodigital.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class EnderecoTest {
    private Validator validator;

    private Endereco enderecoValido;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        enderecoValido = new Endereco();
        enderecoValido.setRua("Rua dos Testes");
        enderecoValido.setNumero(123);
        enderecoValido.setCidade("Cidade dos Bugs");
        enderecoValido.setEstado("SP");
        enderecoValido.setCep("12344-678");
    }

    @Test
    @DisplayName("Deve rejeitar o CEP pois contem o formato inválido")
    void deveValidarCEP() {
        enderecoValido.setCep("22222222");
        Set<ConstraintViolation<Endereco>> violations = validator.validate(enderecoValido);
        assertFalse(violations.isEmpty(), "CEP é inválido não possuiu os 8 digitos/ou o formato inválido");
    }
}
