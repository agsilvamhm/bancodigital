package com.agsilvamhm.bancodigital.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Cartao {

    private Integer id;
    private Conta conta; // Referência ao objeto Conta
    private String numero;
    private String nomeTitular;
    private LocalDate dataValidade;
    private String cvv;
    private String senha; // Em um sistema real, isso seria um hash
    private TipoCartao tipoCartao;
    private BigDecimal limiteCredito; // Aplicável apenas para cartões de crédito
    private BigDecimal limiteDiarioDebito; // Aplicável apenas para cartões de débito
    private boolean ativo;

    // Construtor, Getters e Setters

    public Cartao() {
    }

    public Cartao(Integer id, Conta conta, String numero, String nomeTitular, LocalDate dataValidade, String cvv, String senha, TipoCartao tipoCartao, BigDecimal limiteCredito, BigDecimal limiteDiarioDebito, boolean ativo) {
        this.id = id;
        this.conta = conta;
        this.numero = numero;
        this.nomeTitular = nomeTitular;
        this.dataValidade = dataValidade;
        this.cvv = cvv;
        this.senha = senha;
        this.tipoCartao = tipoCartao;
        this.limiteCredito = limiteCredito;
        this.limiteDiarioDebito = limiteDiarioDebito;
        this.ativo = ativo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNomeTitular() {
        return nomeTitular;
    }

    public void setNomeTitular(String nomeTitular) {
        this.nomeTitular = nomeTitular;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoCartao getTipoCartao() {
        return tipoCartao;
    }

    public void setTipoCartao(TipoCartao tipoCartao) {
        this.tipoCartao = tipoCartao;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public BigDecimal getLimiteDiarioDebito() {
        return limiteDiarioDebito;
    }

    public void setLimiteDiarioDebito(BigDecimal limiteDiarioDebito) {
        this.limiteDiarioDebito = limiteDiarioDebito;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}