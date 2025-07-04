package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.model.Conta;
import com.agsilvamhm.bancodigital.model.Movimentacao;
import com.agsilvamhm.bancodigital.model.dto.*;
import com.agsilvamhm.bancodigital.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/contas")
public class ContaController {

    private final ContaService contaService;


    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Conta> criarConta(@RequestBody CriarContaRequest request) {
        Conta novaConta = contaService.criarConta(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novaConta.getId())
                .toUri();

        return ResponseEntity.created(location).body(novaConta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Conta> buscarPorId(@PathVariable Long id) {
        Conta conta = contaService.buscarPorId(id);
        return ResponseEntity.ok(conta);
    }

    @GetMapping("/{id}/saldo")
    // A anotação de segurança pode ser mantida
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ContaInfoDTO> consultarInfoConta(@PathVariable Long id) {
        Conta conta = contaService.buscarPorId(id);
        ContaInfoDTO contaInfoDTO = ContaInfoDTO.fromEntity(conta);
        return ResponseEntity.ok(contaInfoDTO);
    }

    @PostMapping("/{id}/transferencia")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Movimentacao> realizarTransferencia(
            @PathVariable Long id,
            @Valid @RequestBody TransferenciaRequestDTO request) {

        Movimentacao recibo = contaService.realizarTransferencia(id, request);
        return ResponseEntity.ok(recibo);
    }

    @PostMapping("/{id}/deposito")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')") // Apenas administradores podem simular um depósito
    public ResponseEntity<Movimentacao> realizarDeposito(
            @PathVariable Long id,
            @Valid @RequestBody DepositoRequestDTO request) {

        Movimentacao recibo = contaService.realizarDeposito(id, request);
        return ResponseEntity.ok(recibo);
    }

    @PostMapping("/{id}/saque")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @authService.podeAcessarConta(#id)")
    public ResponseEntity<Movimentacao> realizarSaque(
            @PathVariable Long id,
            @Valid @RequestBody OperacaoContaDTO request) {

        Movimentacao recibo = contaService.realizarSaque(id, request);
        return ResponseEntity.ok(recibo);
    }

    @PostMapping("/{id}/pix")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @authService.podeAcessarConta(#id)")
    public ResponseEntity<Movimentacao> realizarPix(
            @PathVariable Long id,
            @Valid @RequestBody PixRequestDTO request) {

        Movimentacao recibo = contaService.realizarPix(id, request);
        return ResponseEntity.ok(recibo);
    }

    @GetMapping("/{id}/movimentacoes")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @authService.podeAcessarConta(#id)")
    public ResponseEntity<List<Movimentacao>> listarMovimentacoes(@PathVariable Long id) {
        List<Movimentacao> extrato = contaService.listarMovimentacoesPorConta(id);
        return ResponseEntity.ok(extrato);
    }

    @PutMapping("/{id}/manutencao")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Movimentacao> aplicarTaxaManutencao(@PathVariable Long id) {
        Movimentacao recibo = contaService.aplicarTaxaManutencao(id);
        return ResponseEntity.ok(recibo);
    }

    @PutMapping("/{id}/rendimentos")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Movimentacao> aplicarRendimentos(@PathVariable Long id) {
        Movimentacao recibo = contaService.aplicarRendimentos(id);
        return ResponseEntity.ok(recibo);
    }
}