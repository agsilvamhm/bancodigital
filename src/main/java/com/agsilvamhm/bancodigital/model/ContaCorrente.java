package com.agsilvamhm.bancodigital.model;

public class ContaCorrente extends Conta {
    private double taxaManutencaoMensal;

    public double getTaxaManutencaoMensal() {
        return taxaManutencaoMensal;
    }

    public void setTaxaManutencaoMensal(double taxaManutencaoMensal) {
        this.taxaManutencaoMensal = taxaManutencaoMensal;
    }
}