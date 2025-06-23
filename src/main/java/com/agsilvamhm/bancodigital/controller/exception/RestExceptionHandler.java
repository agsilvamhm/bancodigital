package com.agsilvamhm.bancodigital.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptionHandler {
    private ResponseEntity<ApiError> buildErrorResponse(HttpStatus status, String message, WebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setStatus(status.value());
        apiError.setError(status.getReasonPhrase());
        apiError.setMessage(message);
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(apiError, status);
    }

    // Manipulador para quando uma entidade não é encontrada (ex: cliente com ID X não existe)
    // Retorna HTTP 404 Not Found
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ApiError> handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // Manipulador para violação de chave única (ex: CPF duplicado)
    // Retorna HTTP 409 Conflict
    @ExceptionHandler(CpfDuplicadoException.class)
    public ResponseEntity<ApiError> handleCpfDuplicado(CpfDuplicadoException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // Manipulador para argumentos inválidos (ex: nome do cliente em branco)
    // Retorna HTTP 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // Manipulador genérico para qualquer outra exceção não tratada
    // Retorna HTTP 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, WebRequest request) {
        // É importante logar o stack trace completo para depuração
        // logger.error("Ocorreu um erro inesperado", ex);
        String message = "Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.";
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, request);
    }
}
