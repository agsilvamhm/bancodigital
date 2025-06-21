package com.agsilvamhm.bancodigital.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class ApoliceSeguro {
    @Id
    @GeneratedValue
    private Long numero;
    private LocalDate dataContratacao;
    private BigDecimal valor;
    private String descricao;
    @ManyToOne
    private CartaoCredito cartao;

}
