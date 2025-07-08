package com.agsilvamhm.bancodigital.core.domain.exceptions;

public class CpfDuplicadoException extends RuntimeException {
    public CpfDuplicadoException(String message) {
        super(message);
    }
}