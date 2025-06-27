package com.agsilvamhm.bancodigital.controller.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException{

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CpfDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleCpfDuplicado(CpfDuplicadoException ex, HttpServletRequest request) {
        logger.warn("CPF duplicado: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflito de Dados");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI()); // Melhoria: caminho dinâmico
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handlerBusinessException(IllegalArgumentException ex, HttpServletRequest request) {
        logger.warn("Requisição com argumento ilegal: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put("error", "Requisição Inválida");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handlerNotFoundException(NoSuchElementException ex, HttpServletRequest request) {
        logger.warn("Elemento não encontrado: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Recurso Não Encontrado");
        body.put("message", "O recurso solicitado não foi encontrado."); // Mensagem genérica para o cliente
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        logger.warn("Tentativa de acesso negado ao recurso {}: {}", request.getRequestURI(), ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Acesso Negado");
        body.put("message", "Você não tem permissão para acessar este recurso.");
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleRegistroNaoEncontradoException(EntidadeNaoEncontradaException ex, HttpServletRequest request) {
        logger.warn("Entidade não encontrada: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Registro Não Encontrado");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(CpfInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleRegistroNaoEncontradoException(CpfInvalidoException ex, HttpServletRequest request) {
        logger.warn("CPF Inválido: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "CPF inválido");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, Object>> handlerUnexpectedException(Throwable ex, HttpServletRequest request) {
        logger.error("Erro inesperado no servidor ao processar a requisição para {}", request.getRequestURI(), ex);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Erro Interno do Servidor");
        body.put("message", "Ocorreu um erro inesperado. Por favor, contate o suporte.");
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
