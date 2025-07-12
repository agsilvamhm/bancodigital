package com.agsilvamhm.bancodigital.model;

public enum TipoMovimentacao {
    DEPOSITO(1, "Depósito"),
    SAQUE(-1, "Saque"),
    TRANSFERENCIA(-1, "Transferência"),
    PIX(-1, "PIX"),
    TAXA_MANUTENCAO(-1, "Taxa_Manutencao"),
    RENDIMENTO(1, "Rendimento"),
    COMPRA_CREDITO(-1, "Compra com Cartão de Crédito"), // NOVO: Para despesas de cartão de crédito
    PAGAMENTO_FATURA(-1, "Pagamento de Fatura de Cartão"); // NOVO: Para o pagamento da fatura

    private final int operacao;
    private final String descricao;

    TipoMovimentacao(int operacao, String descricao){
        this.operacao = operacao;
        this.descricao = descricao;
    }

    public int getOperacao(){
        return operacao;
    }

    public String getDescricao() { // Getter para a descrição
        return descricao;
    }
}
