package com.agsilvamhm.bancodigital.model;

import jakarta.persistence.Entity;

@Entity
public class ContaPoupanca extends Conta{
    private double taxaRendimento;

    @Override
    public void aplicarTaxasMensalouRendimentos() {

    }
}
