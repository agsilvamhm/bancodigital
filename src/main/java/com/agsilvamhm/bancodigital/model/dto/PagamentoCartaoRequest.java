package com.agsilvamhm.bancodigital.model.dto;

import java.math.BigDecimal;

public record PagamentoCartaoRequest(
        BigDecimal valor,
        String senha,
        String descricao
) {}
