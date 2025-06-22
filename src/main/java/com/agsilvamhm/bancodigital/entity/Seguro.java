package com.agsilvamhm.bancodigital.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

//@Entity
public class Seguro {
  //  @Id
  //  @GeneratedValue
    private Long id;

    private String tipo;
    private double valor;
    private String descricao;

   // @ManyToOne
    private CartaoCredito cartao;

    private String numeroApolice;
    private LocalDate dataContratacao;
}
