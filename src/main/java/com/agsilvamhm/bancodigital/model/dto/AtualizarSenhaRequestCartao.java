package com.agsilvamhm.bancodigital.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarSenhaRequestCartao(
        @NotBlank(message = "A nova senha não pode estar em branco.")
        @Size(min = 4, max = 20, message = "A senha deve ter entre {min} e {max} caracteres.") // Ajuste os tamanhos conforme sua regra
        String novaSenha
) {}