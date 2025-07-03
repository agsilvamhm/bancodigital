package com.agsilvamhm.bancodigital.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;

// CORREÇÃO: Anotações para ajudar o Jackson (API) a lidar com a herança.
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipoConta" // Este campo será adicionado no JSON para indicar o tipo
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ContaCorrente.class, name = "CORRENTE"),
        @JsonSubTypes.Type(value = ContaPoupanca.class, name = "POUPANCA")
})
public abstract class Conta { // CORREÇÃO: A classe base deve ser abstrata.
    private Long id;

    @JsonBackReference
    private Cliente cliente;

    // CORREÇÃO: Nome do campo ajustado de 'numeroConta' para 'numero'.
    private String numero;
    private String agencia;
    private BigDecimal saldo;

    // CORREÇÃO: Os campos 'tipo' e 'ativa' foram removidos, pois não existem na tabela 'conta'.
    // O tipo é definido pela classe filha (ContaCorrente ou ContaPoupanca).
    // A ativação pode ser um conceito de negócio, mas não está no schema atual.

    // Getters e Setters ajustados
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}