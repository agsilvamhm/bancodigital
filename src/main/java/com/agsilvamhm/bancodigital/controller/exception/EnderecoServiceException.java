package com.agsilvamhm.bancodigital.controller.exception;

public class EnderecoServiceException extends RuntimeException{
    public EnderecoServiceException(String message) {
        super(message);
    }
    public EnderecoServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
