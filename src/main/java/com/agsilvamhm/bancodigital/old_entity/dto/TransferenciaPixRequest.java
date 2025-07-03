package com.agsilvamhm.bancodigital.old_entity.dto;

import java.math.BigDecimal;

public record TransferenciaPixRequest(String numeroContaOrigem,
                                      String numeroContaDestino,
                                      BigDecimal valor) {
}
