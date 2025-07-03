package com.agsilvamhm.bancodigital.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.List;

public class Cliente {
    private Integer id;
    @CPF(message = "CPF inválido.")
    private String cpf;
    @NotNull(message = "O nome não pode ser nulo.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre {min} e {max} caracteres.")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ ]+$", message = "O nome deve conter apenas letras e espaços.")
    private String nome;
    @NotNull(message = "A data de nascimento é obrigatória.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;
    @NotNull(message = "O endereço é obrigatório.")
    @Valid
    private Endereco endereco;
    private CategoriaCliente categoria;
    @JsonManagedReference
    private List<Conta> contas;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCategoria(CategoriaCliente categoria) {
        this.categoria = categoria;
    }

    public void setContas(List<Conta> contas) {
        this.contas = contas;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Integer getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public CategoriaCliente getCategoria() {
        return categoria;
    }

    public List<Conta> getContas() {
        return contas;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
}
