package com.agsilvamhm.bancodigital.entity;

import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class CartaoDebito extends Cartao{
    private double limiteDiario;
    private double gastoDia;

    @Override
    public void realizarPagamento(BigDecimal valor) {

    }
}
