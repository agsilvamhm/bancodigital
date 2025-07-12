package com.agsilvamhm.bancodigital.model;

import java.math.BigDecimal;
import java.math.MathContext;

public enum CategoriaCliente {
    COMUM("Comum", new BigDecimal("12.00"), new BigDecimal("0.005"), new BigDecimal("1000.00")),
    SUPER("Super", new BigDecimal("8.00"), new BigDecimal("0.007"), new BigDecimal("5000.00")),
    PREMIUM("Premium", BigDecimal.ZERO, new BigDecimal("0.009"), new BigDecimal("10000.00"));

    private final String descricao;
    private final BigDecimal taxaManutencao;
    private final BigDecimal taxaRendimentoAnual;
    private final BigDecimal limiteCreditoPadrao;

    CategoriaCliente(String descricao, BigDecimal taxaManutencao, BigDecimal taxaRendimentoAnual, BigDecimal limiteCreditoPadrao) {
        this.descricao = descricao;
        this.taxaManutencao = taxaManutencao;
        this.taxaRendimentoAnual = taxaRendimentoAnual;
        this.limiteCreditoPadrao = limiteCreditoPadrao;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getTaxaManutencao() {
        return taxaManutencao;
    }

    public BigDecimal getTaxaRendimentoAnual() {
        return taxaRendimentoAnual;
    }

    public BigDecimal getLimiteCreditoPadrao() { // Novo getter para o limite
        return limiteCreditoPadrao;
    }

    public BigDecimal getTaxaRendimentoMensalEquivalente() {
        if (this.taxaRendimentoAnual.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        MathContext mc = new MathContext(10);

          BigDecimal base = BigDecimal.ONE.add(this.taxaRendimentoAnual);
        double exponente = 1.0 / 12.0;

        BigDecimal taxaMensal = BigDecimal.valueOf(Math.pow(base.doubleValue(), exponente)).subtract(BigDecimal.ONE, mc);

        return taxaMensal;
    }
}