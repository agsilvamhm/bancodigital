package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.model.Cartao;
import com.agsilvamhm.bancodigital.model.dto.AlterarStatusRequest;
import com.agsilvamhm.bancodigital.model.dto.EmitirCartaoRequest;
import com.agsilvamhm.bancodigital.model.dto.PagamentoCartaoRequest;
import com.agsilvamhm.bancodigital.service.CartaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/cartoes")
public class CartaoController {

    private final CartaoService cartaoService;

    @Autowired
    public CartaoController(CartaoService cartaoService) {
        this.cartaoService = cartaoService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Cartao> emitirCartao(@Valid @RequestBody EmitirCartaoRequest request) {
        Cartao novoCartao = cartaoService.emitirNovoCartao(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoCartao.getId())
                .toUri();
        return ResponseEntity.created(location).body(novoCartao);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @authService.podeAcessarCartao(#id)")
    public ResponseEntity<Cartao> buscarPorId(@PathVariable Integer id) {
        Cartao cartao = cartaoService.buscarPorId(id);
        return ResponseEntity.ok(cartao);
    }

    @PostMapping("/{id}/pagamento")
    @PreAuthorize("@authService.podeAcessarCartao(#id)")
    public ResponseEntity<Void> realizarPagamento(@PathVariable Integer id, @Valid @RequestBody PagamentoCartaoRequest request) {
        cartaoService.realizarPagamento(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @authService.podeAcessarCartao(#id)")
    public ResponseEntity<Void> alterarStatus(@PathVariable Integer id, @RequestBody AlterarStatusRequest request) {
        cartaoService.alterarStatus(id, request.ativo());
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints de Fatura (Não implementados no serviço) ---

    @GetMapping("/{id}/fatura")
    @PreAuthorize("@authService.podeAcessarCartao(#id)")
    public ResponseEntity<Void> consultarFatura(@PathVariable Integer id) {
        // Lógica para consultar fatura não implementada no service
        return ResponseEntity.status(501).build(); // 501 Not Implemented
    }

    @PostMapping("/{id}/fatura/pagamento")
    @PreAuthorize("@authService.podeAcessarCartao(#id)")
    public ResponseEntity<Void> pagarFatura(@PathVariable Integer id) {
        // Lógica para pagar fatura não implementada no service
        return ResponseEntity.status(501).build(); // 501 Not Implemented
    }
}
