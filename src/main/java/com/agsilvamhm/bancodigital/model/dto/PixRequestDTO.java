package com.agsilvamhm.bancodigital.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PixRequestDTO(
        @NotNull(message = "O valor do PIX não pode ser nulo.")
        @Positive(message = "O valor do PIX deve ser positivo.")
        BigDecimal valor,

        @NotBlank(message = "A Chave PIX não pode ser vazia.")
        String chavePix, // Para nosso sistema, será o CPF do destinatário

        String descricao
) {}