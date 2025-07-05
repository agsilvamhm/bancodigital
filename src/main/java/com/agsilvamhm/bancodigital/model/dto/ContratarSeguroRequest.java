package com.agsilvamhm.bancodigital.model.dto;

import java.math.BigDecimal;

public record ContratarSeguroRequest(
        Integer cartaoId,
        BigDecimal valorPremio,
        String cobertura,
        String condicoes
) {}
