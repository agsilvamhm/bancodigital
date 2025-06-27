package com.agsilvamhm.bancodigital.controller;


import com.agsilvamhm.bancodigital.entity.Movimentacao;
import com.agsilvamhm.bancodigital.service.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    @Autowired
    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    /**
     * Endpoint para realizar uma transferência entre contas.
     * @param request O corpo da requisição contendo os dados da transferência.
     * @return ResponseEntity com a movimentação registrada.
     */
    @PostMapping("/transferir")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Movimentacao> transferir(@RequestBody TransferenciaRequest request) {
        Movimentacao movimentacao = movimentacaoService.realizarTransferencia(
                request.getIdContaOrigem(),
                request.getIdContaDestino(),
                request.getValor(),
                request.getDescricao()
        );
        return ResponseEntity.ok(movimentacao);
    }

    /**
     * Endpoint para realizar um depósito em uma conta.
     * @param request O corpo da requisição contendo o ID da conta e o valor.
     * @return ResponseEntity com a movimentação registrada.
     */
    @PostMapping("/depositar")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Movimentacao> depositar(@RequestBody OperacaoRequest request) {
        Movimentacao movimentacao = movimentacaoService.realizarDeposito(
                request.getIdConta(),
                request.getValor()
        );
        return ResponseEntity.ok(movimentacao);
    }

    /**
     * Endpoint para realizar um saque de uma conta.
     * @param request O corpo da requisição contendo o ID da conta e o valor.
     * @return ResponseEntity com a movimentação registrada.
     */
    @PostMapping("/sacar")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Movimentacao> sacar(@RequestBody OperacaoRequest request) {
        Movimentacao movimentacao = movimentacaoService.realizarSaque(
                request.getIdConta(),
                request.getValor()
        );
        return ResponseEntity.ok(movimentacao);
    }

    /**
     * Endpoint para buscar o extrato (histórico de movimentações) de uma conta.
     * @param idConta O ID da conta a ser consultada, passado como parâmetro na URL.
     * @return ResponseEntity com a lista de movimentações.
     */
    @GetMapping("/extrato")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<Movimentacao>> extrato(@RequestParam Integer idConta) {
        List<Movimentacao> extrato = movimentacaoService.gerarExtrato(idConta);
        return ResponseEntity.ok(extrato);
    }

    // --- DTOs (Data Transfer Objects) ---
    // Classes internas estáticas para representar os corpos das requisições.

    /**
     * DTO para requisições de transferência.
     */
    public static class TransferenciaRequest {
        private Integer idContaOrigem;
        private Integer idContaDestino;
        private BigDecimal valor;
        private String descricao;

        // Getters e Setters
        public Integer getIdContaOrigem() { return idContaOrigem; }
        public void setIdContaOrigem(Integer idContaOrigem) { this.idContaOrigem = idContaOrigem; }
        public Integer getIdContaDestino() { return idContaDestino; }
        public void setIdContaDestino(Integer idContaDestino) { this.idContaDestino = idContaDestino; }
        public BigDecimal getValor() { return valor; }
        public void setValor(BigDecimal valor) { this.valor = valor; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }

    /**
     * DTO genérico para requisições de depósito e saque.
     */
    public static class OperacaoRequest {
        private Integer idConta;
        private BigDecimal valor;

        // Getters e Setters
        public Integer getIdConta() { return idConta; }
        public void setIdConta(Integer idConta) { this.idConta = idConta; }
        public BigDecimal getValor() { return valor; }
        public void setValor(BigDecimal valor) { this.valor = valor; }
    }
}
