package com.agsilvamhm.bancodigital.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AtualizarLimiteDiarioRequest(
        @NotNull(message = "O novo limite diário não pode ser nulo.")
        @DecimalMin(value = "0.00", inclusive = true, message = "O limite diário deve ser um valor positivo ou zero.")
        BigDecimal novoLimiteDiario
) {}
