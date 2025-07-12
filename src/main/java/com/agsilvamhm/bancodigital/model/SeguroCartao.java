package com.agsilvamhm.bancodigital.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SeguroCartao {

    private Integer id;
    private Cartao cartao; // Referência ao objeto Cartao
    private String numeroApolice;
    private LocalDateTime dataContratacao;
    private String cobertura;
    private String condicoes;
    private BigDecimal valorPremio;

    public SeguroCartao() {
    }

    public SeguroCartao(Integer id, Cartao cartao, String numeroApolice, LocalDateTime dataContratacao, String cobertura, String condicoes, BigDecimal valorPremio) {
        this.id = id;
        this.cartao = cartao;
        this.numeroApolice = numeroApolice;
        this.dataContratacao = dataContratacao;
        this.cobertura = cobertura;
        this.condicoes = condicoes;
        this.valorPremio = valorPremio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cartao getCartao() {
        return cartao;
    }

    public void setCartao(Cartao cartao) {
        this.cartao = cartao;
    }

    public String getNumeroApolice() {
        return numeroApolice;
    }

    public void setNumeroApolice(String numeroApolice) {
        this.numeroApolice = numeroApolice;
    }

    public LocalDateTime getDataContratacao() {
        return dataContratacao;
    }

    public void setDataContratacao(LocalDateTime dataContratacao) {
        this.dataContratacao = dataContratacao;
    }

    public String getCobertura() {
        return cobertura;
    }

    public void setCobertura(String cobertura) {
        this.cobertura = cobertura;
    }

    public String getCondicoes() {
        return condicoes;
    }

    public void setCondicoes(String condicoes) {
        this.condicoes = condicoes;
    }

    public BigDecimal getValorPremio() {
        return valorPremio;
    }

    public void setValorPremio(BigDecimal valorPremio) {
        this.valorPremio = valorPremio;
    }
}
