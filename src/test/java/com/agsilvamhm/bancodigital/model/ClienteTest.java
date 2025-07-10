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

        String novaDataEmString = "25/12/2000";
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

    @Test
    @DisplayName("Deve rejeitar o CPF pois é inválido")
    void deveValidarCPF() {
        clienteValido.setCpf("11111111111");
        Set<ConstraintViolation<Cliente>> violations = validator.validate(clienteValido);
        assertFalse(violations.isEmpty(), "CPF inválido");
    }

    @Test
    @DisplayName("Deve rejeitar o Nome pois contem numeros")
    void deveValidarNome() {
        clienteValido.setNome("Manoel *25 ");
        Set<ConstraintViolation<Cliente>> violations = validator.validate(clienteValido);
        assertFalse(violations.isEmpty(), "Nome inválido pois contêm caracteres especiais");
    }

    @Test
    @DisplayName("Deve rejeitar o Nome pois contem mais de 100 carateres")
    void deveValidarNomeExtencao() {
        clienteValido.setNome("Pedro de Alcântara João Carlos Leopoldo Salvador Bibiano Francisco Xavier de Paula Leocádio Miguel Gabriel Rafael Gonzaga");
        Set<ConstraintViolation<Cliente>> violations = validator.validate(clienteValido);
        assertFalse(violations.isEmpty(), "Nome inválido pois contêm caracteres especiais");
    }

    @Test
    @DisplayName("Deve rejeitar o CEP pois contem o formato invalido")
    void deveValidarCEP() {
        enderecoValido.setCep("22222222");
        clienteValido.setEndereco(enderecoValido);
        Set<ConstraintViolation<Cliente>> violations = validator.validate(clienteValido);
        assertFalse(violations.isEmpty(), "CEP é inválido não possuiu os 8 digitos");
    }




}