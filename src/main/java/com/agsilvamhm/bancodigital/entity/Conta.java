package com.agsilvamhm.bancodigital.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public abstract class Conta {

    private Integer id; // ID único da conta (gerado pelo banco)
    private String numero; // Número da conta (ex: "00123-4")
    private String agencia; // Agência (ex: "0001")
    private double saldo;
    private Cliente cliente; // Associação com o dono da conta
    private List<Movimentacao> movimentacoes; // Histórico de transações

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Movimentacao> getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(List<Movimentacao> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }

    // Getters e Setters...

    // Método abstrato que força as subclasses a definirem suas próprias regras
    // de operações mensais (taxas, rendimentos, etc.)
    public abstract void aplicarOperacoesMensais();
}