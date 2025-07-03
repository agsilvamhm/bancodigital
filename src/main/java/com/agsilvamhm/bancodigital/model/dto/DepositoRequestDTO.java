package com.agsilvamhm.bancodigital.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositoRequestDTO(
        @NotNull(message = "O valor do depósito não pode ser nulo.")
        @Positive(message = "O valor do depósito deve ser positivo.")
        BigDecimal valor,
        String descricao
) {}