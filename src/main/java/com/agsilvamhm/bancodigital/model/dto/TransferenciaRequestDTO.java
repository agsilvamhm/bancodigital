package com.agsilvamhm.bancodigital.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferenciaRequestDTO(

        @NotNull(message = "O valor da transferência não pode ser nulo.")
        @Positive(message = "O valor da transferência deve ser positivo.")
        BigDecimal valor,

        @NotBlank(message = "O número da conta de destino não pode ser vazio.")
        String contaDestinoNumero,

        String descricao
) {}
