package com.agsilvamhm.bancodigital.controller.exception;

public class CpfInvalidoException extends RuntimeException {
    public CpfInvalidoException(String message) {
        super(message);
    }
}