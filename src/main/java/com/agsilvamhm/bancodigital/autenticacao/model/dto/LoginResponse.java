package com.agsilvamhm.bancodigital.autenticacao.model.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
