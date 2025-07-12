package com.agsilvamhm.bancodigital.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PagarFaturaRequest(
        @NotNull(message = "O valor do pagamento não pode ser nulo.")
        @DecimalMin(value = "0.01", message = "O valor do pagamento deve ser maior que zero.")
        BigDecimal valorPagamento
) {}
