package com.agsilvamhm.bancodigital.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class CartaoCredito extends Cartao{
    private double limite;
    private double totalGastoMes;

    @OneToMany(mappedBy = "cartao")
    private List<Seguro> seguros;

    @Override
    public void realizarPagamento(BigDecimal valor) {

    }
}
