package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.component.ProcessamentoMensalScheduler;
import com.agsilvamhm.bancodigital.entity.Conta;
import com.agsilvamhm.bancodigital.entity.ContaCorrente;
import com.agsilvamhm.bancodigital.entity.ContaPoupanca;
import com.agsilvamhm.bancodigital.service.ContaService;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/contas")
public class ContaController {

    private final ContaService contaService;
    private final ProcessamentoMensalScheduler processamentoScheduler;

    public ContaController(ContaService contaService, ProcessamentoMensalScheduler processamentoScheduler) {
        this.contaService = contaService;
        this.processamentoScheduler = processamentoScheduler;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ContaCorrente.class, name = "CORRENTE"),
            @JsonSubTypes.Type(value = ContaPoupanca.class, name = "POUPANCA")
    })
    abstract class ContaPolimorfica extends Conta {}

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Conta> criarConta(@RequestBody ContaPolimorfica conta) {
        Conta novaConta = contaService.criarConta(conta);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novaConta.getId())
                .toUri();
        return ResponseEntity.created(location).body(novaConta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Conta> buscarPorId(@PathVariable Integer id) {
        Conta conta = contaService.buscarPorId(id);
        return ResponseEntity.ok(conta);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<Conta>> buscarContasPorCliente(@RequestParam Integer clienteId) {
        List<Conta> contas = contaService.buscarContasPorCliente(clienteId);
        return ResponseEntity.ok(contas);
    }

    @PostMapping("/processar-taxas")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<String> dispararProcessamentoTaxas() {
        processamentoScheduler.processarTaxasDeManutencao();
        return ResponseEntity.accepted().body("Processamento de taxas iniciado.");
    }

    @PostMapping("/processar-rendimentos")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<String> dispararProcessamentoRendimentos() {
        processamentoScheduler.processarRendimentosPoupanca();
        return ResponseEntity.accepted().body("Processamento de rendimentos iniciado.");
    }
}