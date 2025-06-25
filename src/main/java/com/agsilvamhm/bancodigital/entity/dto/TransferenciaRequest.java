package com.agsilvamhm.bancodigital.entity.dto;

import java.math.BigDecimal;

public class TransferenciaRequest {
    private Integer idContaDestino;
    private BigDecimal valor;

    public Integer getIdContaDestino() {
        return idContaDestino;
    }

    public void setIdContaDestino(Integer idContaDestino) {
        this.idContaDestino = idContaDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
