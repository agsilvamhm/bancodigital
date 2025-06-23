package com.agsilvamhm.bancodigital.controller.exception;

public class EntidadeNaoEncontradaException extends RepositorioException {
    public EntidadeNaoEncontradaException(String message) {
        super(message, null);
    }
}