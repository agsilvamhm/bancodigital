package com.agsilvamhm.bancodigital.model;

public class ContaCorrente extends Conta {
    // CORREÇÃO: Adicionar o campo para armazenar o valor da taxa.
    private double taxaManutencaoMensal;

    // Getters e Setters corretos
    public double getTaxaManutencaoMensal() {
        return taxaManutencaoMensal;
    }

    public void setTaxaManutencaoMensal(double taxaManutencaoMensal) {
        this.taxaManutencaoMensal = taxaManutencaoMensal;
    }
}