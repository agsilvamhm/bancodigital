package com.agsilvamhm.bancodigital.model;

import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class CartapDebito extends Cartao{
    private double limiteDiario;
    private double gastoDia;

    @Override
    public void realizarPagamento(BigDecimal valor) {

    }
}
