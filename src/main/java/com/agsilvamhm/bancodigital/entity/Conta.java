package com.agsilvamhm.bancodigital.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

public abstract class Conta {

    private Integer id;
    private String numero;
    private String agencia;
    private double saldo;
    @JsonBackReference
    private Cliente cliente;
    private double taxa_manutencao_mensal;
    private double taxa_rendimento_mensal;

    public double getTaxa_manutencao_mensal() {
        return taxa_manutencao_mensal;
    }

    public void setTaxa_manutencao_mensal(double taxa_manutencao_mensal) {
        this.taxa_manutencao_mensal = taxa_manutencao_mensal;
    }

    public double getTaxa_rendimento_mensal() {
        return taxa_rendimento_mensal;
    }

    public void setTaxa_rendimento_mensal(double taxa_rendimento_mensal) {
        this.taxa_rendimento_mensal = taxa_rendimento_mensal;
    }

    public void debitar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor do débito deve ser positivo.");
        }
        this.saldo -= valor;
    }

    public void creditar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor do crédito deve ser positivo.");
        }
        this.saldo += valor;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
}