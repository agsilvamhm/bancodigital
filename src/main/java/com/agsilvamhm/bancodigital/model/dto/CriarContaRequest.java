package com.agsilvamhm.bancodigital.model.dto;

import com.agsilvamhm.bancodigital.model.TipoConta;

public record CriarContaRequest(
        Integer clienteId,
        String numero,
        String agencia,
        TipoConta tipoConta

) {}