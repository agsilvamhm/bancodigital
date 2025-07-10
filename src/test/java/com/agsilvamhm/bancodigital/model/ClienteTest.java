package com.agsilvamhm.bancodigital.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    private Validator validator;

    private Cliente clienteValido;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        Endereco enderecoValido = new Endereco();
        enderecoValido.setRua("Rua dos Testes");
        enderecoValido.setNumero(123);
        enderecoValido.setCidade("Cidade dos Bugs");
        enderecoValido.setEstado("SP");
        enderecoValido.setCep("12345-678");

        String novaDataEmString = "25/12/2001";
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate novaData = LocalDate.parse(novaDataEmString, formatador);

        clienteValido = new Cliente();
        clienteValido.setId(1);
        clienteValido.setCpf("841.938.440-21");
        clienteValido.setNome("Nome Válido da Silva");
        clienteValido.setDataNascimento(novaData);
        clienteValido.setCategoria(CategoriaCliente.COMUM);
        clienteValido.setContas(new ArrayList<>());
        clienteValido.setEndereco(enderecoValido);
    }

    @Test
    @DisplayName("Deve criar um cliente válido sem violar nenhuma restrição")
    void deveCriarClienteValido() {
        Set<ConstraintViolation<Cliente>> violations = validator.validate(clienteValido);
        assertTrue(violations.isEmpty(), "Cliente válido não deveria ter violações de restrição");
    }



}