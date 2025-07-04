package com.agsilvamhm.bancodigital.model.dto;

import com.agsilvamhm.bancodigital.model.Conta;

import java.math.BigDecimal;

public record ContaInfoDTO(
        String numero,
        String agencia,
        BigDecimal saldo
) {

    public static ContaInfoDTO fromEntity(Conta conta) {
        return new ContaInfoDTO(
                conta.getNumero(),
                conta.getAgencia(),
                conta.getSaldo()
        );
    }
}