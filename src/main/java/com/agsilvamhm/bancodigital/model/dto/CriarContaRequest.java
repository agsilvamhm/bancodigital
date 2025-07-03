package com.agsilvamhm.bancodigital.model.dto;

import com.agsilvamhm.bancodigital.old_entity.TipoConta;

public record CriarContaRequest(
        Integer clienteId,
        String numero,
        String agencia,
        TipoConta tipoConta,
        Double taxaManutencao, // Pode ser nulo se for poupança
        Double taxaRendimento  // Pode ser nulo se for corrente
) {}