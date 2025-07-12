package com.agsilvamhm.bancodigital.model;

import java.time.LocalDateTime;

public class Movimentacao {
    private Integer id;
    private TipoMovimentacao tipo;
    private double valor;
    private LocalDateTime dataHora;
    private Conta contaOrigem; // Pode ser nulo para depósitos, ou para compras de crédito (onde a conta é afetada na fatura, não diretamente no saldo)
    private Conta contaDestino; // Pode ser nulo para saques
    private Cartao cartao; // NOVO CAMPO: Referência ao objeto Cartao que gerou a movimentação
    private String descricao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Conta getContaOrigem() {
        return contaOrigem;
    }

    public void setContaOrigem(Conta contaOrigem) {
        this.contaOrigem = contaOrigem;
    }

    public Conta getContaDestino() {
        return contaDestino;
    }

    public void setContaDestino(Conta contaDestino) {
        this.contaDestino = contaDestino;
    }

    public Cartao getCartao() { // NOVO GETTER
        return cartao;
    }

    public void setCartao(Cartao cartao) { // NOVO SETTER
        this.cartao = cartao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}