package com.agsilvamhm.bancodigital.entity.dto;

import java.math.BigDecimal;

public class OperacaoValorRequest {
    private BigDecimal valor;

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
