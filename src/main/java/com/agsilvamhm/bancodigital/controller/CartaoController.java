package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.entity.Cartao;
import com.agsilvamhm.bancodigital.entity.TipoCartao;
import com.agsilvamhm.bancodigital.service.CartaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cartoes")
public class CartaoController {
    private final CartaoService cartaoService;

    public CartaoController(CartaoService cartaoService) { this.cartaoService = cartaoService; }

    @PostMapping
    public ResponseEntity<Cartao> emitirCartao(@RequestBody EmissaoRequest request) {
        return ResponseEntity.ok(cartaoService.emitirCartao(request.getIdConta(), request.getTipoCartao()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Cartao> ativarDesativar(@PathVariable Integer id, @RequestBody StatusRequest request) {
        return ResponseEntity.ok(cartaoService.ativarDesativarCartao(id, request.isAtivo()));
    }

    @PatchMapping("/{id}/senha")
    public ResponseEntity<Void> alterarSenha(@PathVariable Integer id, @RequestBody SenhaRequest request) {
        cartaoService.alterarSenha(id, request.getNovaSenha());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/limite")
    public ResponseEntity<Cartao> ajustarLimite(@PathVariable Integer id, @RequestBody LimiteRequest request) {
        return ResponseEntity.ok(cartaoService.ajustarLimite(id, request.getNovoLimite()));
    }

    @GetMapping
    public ResponseEntity<List<Cartao>> buscarPorConta(@RequestParam Integer idConta) {
        return ResponseEntity.ok(cartaoService.buscarCartoesPorConta(idConta));
    }

    // DTOs
    static class EmissaoRequest {
        private Integer idConta;
        private TipoCartao tipoCartao;

        public Integer getIdConta() { return idConta; }
        public void setIdConta(Integer idConta) { this.idConta = idConta; }
        public TipoCartao getTipoCartao() { return tipoCartao; }
        public void setTipoCartao(TipoCartao tipoCartao) { this.tipoCartao = tipoCartao; }
    }

    static class StatusRequest {
        private boolean ativo;

        public boolean isAtivo() { return ativo; }
        public void setAtivo(boolean ativo) { this.ativo = ativo; }
    }

    static class SenhaRequest {
        private String novaSenha;

        public String getNovaSenha() { return novaSenha; }
        public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }
    }

    static class LimiteRequest {
        private BigDecimal novoLimite;

        public BigDecimal getNovoLimite() { return novoLimite; }
        public void setNovoLimite(BigDecimal novoLimite) { this.novoLimite = novoLimite; }
    }
}