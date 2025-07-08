package com.agsilvamhm.bancodigital.core.domain.model;

import com.agsilvamhm.bancodigital.model.CategoriaCliente;
import com.agsilvamhm.bancodigital.model.Conta;

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

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public com.agsilvamhm.bancodigital.core.domain.model.Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
    public CategoriaCliente getCategoria() { return categoria; }
    public void setCategoria(CategoriaCliente categoria) { this.categoria = categoria; }
    public List<Conta> getContas() { return contas; }
    public void setContas(List<Conta> contas) { this.contas = contas; }
}