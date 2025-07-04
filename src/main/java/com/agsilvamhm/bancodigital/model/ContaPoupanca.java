package com.agsilvamhm.bancodigital.model;

public class ContaPoupanca extends Conta {
    private double taxaRendimentoMensal;

    public double getTaxaRendimentoMensal() {
        return taxaRendimentoMensal;
    }

    public void setTaxaRendimentoMensal(double taxaRendimentoMensal) {
        this.taxaRendimentoMensal = taxaRendimentoMensal;
    }
}

