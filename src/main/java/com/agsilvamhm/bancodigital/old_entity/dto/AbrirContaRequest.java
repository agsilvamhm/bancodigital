package com.agsilvamhm.bancodigital.old_entity.dto;

import com.agsilvamhm.bancodigital.old_entity.TipoConta;

public record AbrirContaRequest(Long clienteId,
                                String agencia,
                                String numeroConta,
                                TipoConta tipoConta
) {
}
