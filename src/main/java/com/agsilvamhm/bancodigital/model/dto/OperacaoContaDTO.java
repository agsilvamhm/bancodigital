package com.agsilvamhm.bancodigital.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OperacaoContaDTO(
        @NotNull(message = "O valor da operação não pode ser nulo.")
        @Positive(message = "O valor da operação deve ser positivo.")
        BigDecimal valor,

        String descricao
) {}
