package com.agsilvamhm.bancodigital.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

//@Entity
//@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Conta {
 //   @Id
  //  @GeneratedValue
    private Long id;
    private BigDecimal saldo;
 //   @ManyToOne
    private Cliente cliente;
 //   @OneToMany(mappedBy = "conta")
    private List<Cartao> cartoes;

    public abstract void aplicarTaxasMensalouRendimentos();

}
