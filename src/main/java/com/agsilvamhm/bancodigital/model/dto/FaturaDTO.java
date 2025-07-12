package com.agsilvamhm.bancodigital.model.dto;

import com.agsilvamhm.bancodigital.model.Movimentacao;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record FaturaDTO(
        Integer cartaoId,             // ID do cartão
        String numeroCartao,          // Número do cartão (pode ser mascarado)
        YearMonth mesReferencia,      // Mês e ano da fatura (ex: 2025-07)
        BigDecimal totalGasto,        // Total de gastos no mês (antes da taxa)
        BigDecimal taxaUtilizacao,    // Valor da taxa de utilização, se aplicável
        BigDecimal valorTotalFatura,  // Valor total a pagar na fatura (gastos + taxas)
        List<Movimentacao> detalhesDasMovimentacoes // Lista de movimentações que compõem a fatura
) {
    // Métodos utilitários ou construtores adicionais podem ser adicionados aqui, se necessário.
    // Para um Record, o construtor canônico é gerado automaticamente.
}