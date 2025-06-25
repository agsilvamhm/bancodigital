package com.agsilvamhm.bancodigital.entity.dto;

import java.math.BigDecimal;

public class PixRequest {
    private String chavePix; // Ex: CPF, e-mail, telefone
    private BigDecimal valor;

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
