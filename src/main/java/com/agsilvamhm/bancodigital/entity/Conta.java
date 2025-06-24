package com.agsilvamhm.bancodigital.entity;

import java.math.BigDecimal;
import java.util.List;

public abstract class Conta {
    private Long id;
    private BigDecimal saldo;
    private Cliente cliente;
    private List<Cartao> cartoes;
    public abstract void aplicarTaxasMensalouRendimentos();
}

