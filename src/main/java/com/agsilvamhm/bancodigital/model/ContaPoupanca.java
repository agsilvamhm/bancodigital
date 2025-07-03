package com.agsilvamhm.bancodigital.model;

public class ContaPoupanca extends Conta {
    // CORREÇÃO: Adicionar o campo para armazenar o valor do rendimento.
    private double taxaRendimentoMensal;

    // Getters e Setters corretos
    public double getTaxaRendimentoMensal() {
        return taxaRendimentoMensal;
    }

    public void setTaxaRendimentoMensal(double taxaRendimentoMensal) {
        this.taxaRendimentoMensal = taxaRendimentoMensal;
    }
}

