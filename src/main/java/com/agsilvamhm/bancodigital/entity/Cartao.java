package com.agsilvamhm.bancodigital.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cartao {
    @Id
    @GeneratedValue
    private Long id;
    private boolean ativo;
    private String senha;
    @ManyToOne
    private Conta conta;

    public abstract void realizarPagamento(BigDecimal valor);

}
