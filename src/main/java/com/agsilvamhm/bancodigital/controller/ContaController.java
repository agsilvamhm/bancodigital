package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.component.ProcessamentoMensalScheduler;
import com.agsilvamhm.bancodigital.dao.ContaNovaDTO;
import com.agsilvamhm.bancodigital.entity.Cliente;
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

   /* @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ContaCorrente.class, name = "CORRENTE"),
            @JsonSubTypes.Type(value = ContaPoupanca.class, name = "POUPANCA")
    })
    abstract class ContaPolimorfica extends Conta {}*/

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Conta> criarConta(@RequestBody ContaNovaDTO conta) {
        Conta contaParaSalvar = converterDtoParaEntidade(conta);
        Conta novaConta = contaService.criarConta(contaParaSalvar);
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

    private Conta converterDtoParaEntidade(ContaNovaDTO dto) {
        Conta conta;

        // Decide qual tipo de conta instanciar
        if ("CORRENTE".equalsIgnoreCase(dto.getTipo())) {
            conta = new ContaCorrente();
        } else if ("POUPANCA".equalsIgnoreCase(dto.getTipo())) {
            conta = new ContaPoupanca();
        } else {
            throw new IllegalArgumentException("Tipo de conta inválido: " + dto.getTipo());
        }

        // Copia os dados do DTO para a entidade
        conta.setNumero(dto.getNumero());
        conta.setAgencia(dto.getAgencia());
        conta.setSaldo(dto.getSaldo());

        // A MÁGICA ACONTECE AQUI:
        // Crie um objeto Cliente "proxy" que contém apenas o ID.
        // Isso é suficiente para a camada de persistência, pois o ContaDao.salvar
        // só precisa do ID para a chave estrangeira (id_cliente).
        Cliente clienteAssociado = new Cliente();
        clienteAssociado.setId(dto.getClienteId());
        conta.setCliente(clienteAssociado);

        return conta;
    }
}