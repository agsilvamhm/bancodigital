package com.agsilvamhm.bancodigital.model.dto;

import com.agsilvamhm.bancodigital.model.TipoCartao;

import java.math.BigDecimal;

public record EmitirCartaoRequest(
        Integer contaId,
        TipoCartao tipoCartao,
        String senha, // A senha deve vir do cliente
        BigDecimal limiteCredito, // Opcional, apenas para crédito
        BigDecimal limiteDiarioDebito // Opcional, apenas para débito
) {}
