package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.model.Conta;
import com.agsilvamhm.bancodigital.model.dto.CriarContaRequest;
import com.agsilvamhm.bancodigital.old_component.ProcessamentoMensalScheduler;
import com.agsilvamhm.bancodigital.service.ContaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
}