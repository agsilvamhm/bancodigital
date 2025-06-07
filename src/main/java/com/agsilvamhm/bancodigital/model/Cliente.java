package com.agsilvamhm.bancodigital.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Cliente {
    @Id
    private String cpf;
    private String nome;
    private LocalDate dataNascimento;
    @Embedded
    private Endereco endereco;
    @Enumerated(EnumType.STRING)
    private CategoriaCliente categoria;
    @OneToMany(mappedBy = "cliente")
    private List<Conta> contas;

}
