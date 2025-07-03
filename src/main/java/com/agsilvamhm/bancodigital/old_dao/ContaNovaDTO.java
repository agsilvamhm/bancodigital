package com.agsilvamhm.bancodigital.old_dao;

public class ContaNovaDTO {

    private String numero;
    private String agencia;
    private double saldo;
    private Integer clienteId;
    private String tipo;
    private double taxa_manutencao_mensal;
    private double taxa_rendimento_mensal;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

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
}
