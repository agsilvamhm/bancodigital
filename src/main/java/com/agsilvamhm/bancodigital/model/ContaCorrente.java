package com.agsilvamhm.bancodigital.model;

import jakarta.persistence.Entity;

@Entity
public class ContaCorrente extends Conta{
    private double taxaManutencao;

    @Override
    public void aplicarTaxasMensalouRendimentos() {

    }
}
