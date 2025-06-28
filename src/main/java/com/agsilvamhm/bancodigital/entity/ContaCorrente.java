package com.agsilvamhm.bancodigital.entity;

public class ContaCorrente extends Conta {

    private double taxaManutencaoMensal;

    public double getTaxaManutencaoMensal() {
        return taxaManutencaoMensal;
    }

    public void setTaxaManutencaoMensal(double taxaManutencaoMensal) {
        this.taxaManutencaoMensal = taxaManutencaoMensal;
    }

    public void aplicarOperacoesMensais() {
        // Lógica para descontar a taxa de manutenção do saldo
        double saldoAtual = getSaldo();
        setSaldo(saldoAtual - this.taxaManutencaoMensal);
        // Aqui você também criaria uma Movimentacao para registrar a cobrança da taxa
        System.out.println("Taxa de manutenção de " + this.taxaManutencaoMensal + " aplicada.");
    }
}
