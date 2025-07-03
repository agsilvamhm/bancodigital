package com.agsilvamhm.bancodigital.old_entity;

import com.agsilvamhm.bancodigital.model.Conta;

import java.time.LocalDate;

public class CartaoCredito {

    private Integer id;
    private Conta conta; // Vinculado a uma conta
    private String numero;
    private LocalDate validade;
    private String cvv;
    private double limiteCredito;
    private double faturaAtual;
    private boolean possuiSeguroViagem;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Conta getConta() { return conta; }
    public void setConta(Conta conta) { this.conta = conta; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getValidade() { return validade; }
    public void setValidade(LocalDate validade) { this.validade = validade; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public double getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(double limiteCredito) { this.limiteCredito = limiteCredito; }
    public double getFaturaAtual() { return faturaAtual; }
    public void setFaturaAtual(double faturaAtual) { this.faturaAtual = faturaAtual; }
    public boolean isPossuiSeguroViagem() { return possuiSeguroViagem; }
    public void setPossuiSeguroViagem(boolean possuiSeguroViagem) { this.possuiSeguroViagem = possuiSeguroViagem; }
}