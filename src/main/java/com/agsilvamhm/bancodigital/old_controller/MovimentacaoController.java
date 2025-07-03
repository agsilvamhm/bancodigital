package com.agsilvamhm.bancodigital.old_controller;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/movimentacoes")
public class MovimentacaoController {
/*

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


    @PostMapping("/depositar")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Movimentacao> depositar(@RequestBody OperacaoRequest request) {
        Movimentacao movimentacao = movimentacaoService.realizarDeposito(
                request.getIdConta(),
                request.getValor()
        );
        return ResponseEntity.ok(movimentacao);
    }


    @PostMapping("/sacar")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Movimentacao> sacar(@RequestBody OperacaoRequest request) {
        Movimentacao movimentacao = movimentacaoService.realizarSaque(
                request.getIdConta(),
                request.getValor()
        );
        return ResponseEntity.ok(movimentacao);
    }


    @GetMapping("/extrato")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<Movimentacao>> extrato(@RequestParam Integer idConta) {
        List<Movimentacao> extrato = movimentacaoService.gerarExtrato(idConta);
        return ResponseEntity.ok(extrato);
    }

    // --- DTOs (Data Transfer Objects) ---
    // Classes internas estáticas para representar os corpos das requisições.


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


    public static class OperacaoRequest {
        private Integer idConta;
        private BigDecimal valor;

        // Getters e Setters
        public Integer getIdConta() { return idConta; }
        public void setIdConta(Integer idConta) { this.idConta = idConta; }
        public BigDecimal getValor() { return valor; }
        public void setValor(BigDecimal valor) { this.valor = valor; }
    }  */
}
