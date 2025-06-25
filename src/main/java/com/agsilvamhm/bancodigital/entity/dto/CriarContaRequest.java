package com.agsilvamhm.bancodigital.entity.dto;

import java.math.BigDecimal;

public class CriarContaRequest {
    private Integer idCliente;
    private String agencia;
    private String numeroConta;
    private BigDecimal saldoInicial;
    private TipoConta tipoConta; // Enum para definir o tipo: CORRENTE ou POUPANCA

    // Atributos específicos de cada tipo de conta (podem ser nulos)
    private BigDecimal taxaManutencao; // Apenas para Conta Corrente
    private BigDecimal taxaRendimento; // Apenas para Conta Poupança

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public TipoConta getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(TipoConta tipoConta) {
        this.tipoConta = tipoConta;
    }

    public BigDecimal getTaxaManutencao() {
        return taxaManutencao;
    }

    public void setTaxaManutencao(BigDecimal taxaManutencao) {
        this.taxaManutencao = taxaManutencao;
    }

    public BigDecimal getTaxaRendimento() {
        return taxaRendimento;
    }

    public void setTaxaRendimento(BigDecimal taxaRendimento) {
        this.taxaRendimento = taxaRendimento;
    }
}
