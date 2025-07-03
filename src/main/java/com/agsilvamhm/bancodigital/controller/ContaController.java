package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.model.Conta;
import com.agsilvamhm.bancodigital.model.Movimentacao;
import com.agsilvamhm.bancodigital.model.dto.ContaInfoDTO;
import com.agsilvamhm.bancodigital.model.dto.CriarContaRequest;
import com.agsilvamhm.bancodigital.model.dto.SaldoDTO;
import com.agsilvamhm.bancodigital.model.dto.TransferenciaRequestDTO;
import com.agsilvamhm.bancodigital.old_component.ProcessamentoMensalScheduler;
import com.agsilvamhm.bancodigital.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

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
        // 1. Em vez de um método específico de saldo, usamos o buscarPorId que já retorna o objeto completo.
        Conta conta = contaService.buscarPorId(id);

        // 2. Usamos o método estático 'fromEntity' para criar o DTO de resposta de forma limpa.
        ContaInfoDTO contaInfoDTO = ContaInfoDTO.fromEntity(conta);

        // 3. Retornamos o novo DTO com status 200 OK.
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
}