package com.agsilvamhm.bancodigital.entity;

import java.time.LocalDate;
import java.util.List;

public class Cliente {
    private Integer id;
    private String cpf;
    private String nome;
    private LocalDate dataNascimento;
    private Endereco endereco;
    private CategoriaCliente categoria;
    private List<Conta> contas;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCategoria(CategoriaCliente categoria) {
        this.categoria = categoria;
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

}
