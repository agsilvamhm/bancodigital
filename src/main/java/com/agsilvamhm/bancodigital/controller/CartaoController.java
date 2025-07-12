package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.model.Cartao;
import com.agsilvamhm.bancodigital.model.dto.*;
import com.agsilvamhm.bancodigital.service.CartaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.YearMonth;

@RestController
@RequestMapping("/cartoes")
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
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Cartao> buscarPorId(@PathVariable Integer id) {
        Cartao cartao = cartaoService.buscarPorId(id);
        return ResponseEntity.ok(cartao);
    }

    @PostMapping("/{id}/pagamento")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> realizarPagamento(@PathVariable Integer id, @Valid @RequestBody PagamentoCartaoRequest request) {
        cartaoService.realizarPagamento(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> alterarStatus(@PathVariable Integer id, @RequestBody AlterarStatusRequest request) {
        cartaoService.alterarStatus(id, request.ativo());
        return ResponseEntity.noContent().build();
    }

    // --- NOVOS ENDPOINTS IMPLEMENTADOS ---

    @PutMapping("/{id}/limite")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> atualizarLimiteCredito(@PathVariable Integer id, @Valid @RequestBody AtualizarLimiteRequest request) {
        cartaoService.atualizarLimiteCredito(id, request.novoLimite());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/senha")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> alterarSenha(@PathVariable Integer id, @Valid @RequestBody AtualizarSenhaRequestCartao request) {
        cartaoService.alterarSenha(id, request.novaSenha());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/fatura")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<FaturaDTO> consultarFatura(
            @PathVariable Integer id,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth mesReferencia) {
        if (mesReferencia == null) {
            mesReferencia = YearMonth.now();
        }
        FaturaDTO fatura = cartaoService.gerarFaturaMensal(id, mesReferencia);
        return ResponseEntity.ok(fatura);
    }

    @PostMapping("/{id}/fatura/pagamento")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> pagarFatura(@PathVariable Integer id, @Valid @RequestBody PagarFaturaRequest request) {
        cartaoService.pagarFatura(id, request.valorPagamento());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/limite-diario")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> atualizarLimiteDiarioDebito(@PathVariable Integer id, @Valid @RequestBody AtualizarLimiteDiarioRequest request) {
        cartaoService.atualizarLimiteDiarioDebito(id, request.novoLimiteDiario());
        return ResponseEntity.noContent().build();
    }
}