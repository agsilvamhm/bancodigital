package com.agsilvamhm.bancodigital.model;

public enum CategoriaCliente {
    COMUM("Comum"),
    SUPER("Super"),
    PREMIUM("Premium");

    private String descricao;

    CategoriaCliente(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
