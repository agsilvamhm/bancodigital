package com.agsilvamhm.bancodigital.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum CategoriaCliente {
    COMUM("Comum", new BigDecimal("12.00"), new BigDecimal("0.005")),
    SUPER("Super", new BigDecimal("8.00"), new BigDecimal("0.007")),
    PREMIUM("Premium", BigDecimal.ZERO, new BigDecimal("0.009"));

    private final String descricao;
    private final BigDecimal taxaManutencao;
    private final BigDecimal taxaRendimentoAnual;

    CategoriaCliente(String descricao, BigDecimal taxaManutencao, BigDecimal taxaRendimentoAnual) {
        this.descricao = descricao;
        this.taxaManutencao = taxaManutencao;
        this.taxaRendimentoAnual = taxaRendimentoAnual;
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

    public BigDecimal getTaxaRendimentoMensalEquivalente() {
        if (this.taxaRendimentoAnual.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        double base = BigDecimal.ONE.add(this.taxaRendimentoAnual).doubleValue();
        double exponente = 1.0 / 12.0;
        double taxaMensalDouble = Math.pow(base, exponente) - 1.0;

        return new BigDecimal(String.valueOf(taxaMensalDouble));
    }
}
