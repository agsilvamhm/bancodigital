package com.agsilvamhm.bancodigital.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Conta {

    private Integer id;
    private Integer idCliente;
    private String agencia;
    private String numeroConta;
    private BigDecimal saldo;
    private LocalDateTime dataAbertura; // Atributo novo

    public Conta() {
    }

    // Construtor atualizado para incluir dataAbertura
    public Conta(Integer id, Integer idCliente, String agencia, String numeroConta, BigDecimal saldo, LocalDateTime dataAbertura) {
        this.id = id;
        this.idCliente = idCliente;
        this.agencia = agencia;
        this.numeroConta = numeroConta;
        this.saldo = saldo;
        this.dataAbertura = dataAbertura;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }
    public String getNumeroConta() { return numeroConta; }
    public void setNumeroConta(String numeroConta) { this.numeroConta = numeroConta; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    // Getter e Setter para o novo atributo
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return Objects.equals(agencia, conta.agencia) && Objects.equals(numeroConta, conta.numeroConta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencia, numeroConta);
    }

    // toString atualizado
    @Override
    public String toString() {
        return "Conta{" +
                "id=" + id +
                ", idCliente=" + idCliente +
                ", agencia='" + agencia + '\'' +
                ", numeroConta='" + numeroConta + '\'' +
                ", saldo=" + saldo +
                ", dataAbertura=" + dataAbertura + // Novo campo
                '}';
    }
}