package com.agsilvamhm.bancodigital.entity;

public class ContaPoupanca extends Conta {

    private double taxaRendimentoMensal; // Em percentual, ex: 0.005 para 0.5%

    public double getTaxaRendimentoMensal() {
        return taxaRendimentoMensal;
    }

    public void setTaxaRendimentoMensal(double taxaRendimentoMensal) {
        this.taxaRendimentoMensal = taxaRendimentoMensal;
    }

    // Getters e Setters...

    @Override
    public void aplicarOperacoesMensais() {
        // Lógica para creditar o rendimento ao saldo
        double rendimento = getSaldo() * this.taxaRendimentoMensal;
        double saldoAtual = getSaldo();
        setSaldo(saldoAtual + rendimento);
        // Aqui você também criaria uma Movimentacao para registrar o crédito do rendimento
        System.out.println("Rendimento de " + rendimento + " creditado.");
    }
}

