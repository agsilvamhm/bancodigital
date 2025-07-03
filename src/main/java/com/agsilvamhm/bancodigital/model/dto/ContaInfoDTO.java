package com.agsilvamhm.bancodigital.model.dto;

import com.agsilvamhm.bancodigital.model.Conta;

import java.math.BigDecimal;

public record ContaInfoDTO(
        String numero,
        String agencia,
        BigDecimal saldo
) {
    /**
     * Método de conveniência para converter uma entidade Conta neste DTO.
     * Facilita a criação do DTO no controller.
     *
     * @param conta A entidade Conta a ser convertida.
     * @return Um novo objeto ContaInfoDTO.
     */
    public static ContaInfoDTO fromEntity(Conta conta) {
        return new ContaInfoDTO(
                conta.getNumero(),
                conta.getAgencia(),
                conta.getSaldo()
        );
    }
}