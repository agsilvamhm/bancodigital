package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.entity.Conta;
import com.agsilvamhm.bancodigital.service.ContaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para gerenciar operações relacionadas a Contas.
 *
 * Expõe endpoints para criar, buscar, listar, atualizar e deletar contas,
 * com acesso restrito a usuários com a autoridade 'SCOPE_ADMIN'.
 */
@RestController
@RequestMapping(value = "/api/v1/contas")
public class ContaController {

    private final ContaService contaService;

    /**
     * Construtor para injeção de dependência do ContaService.
     * @param contaService O serviço que encapsula a lógica de negócio para contas.
     */
    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    /**
     * Cria uma nova conta.
     * @param conta O objeto Conta a ser criado, fornecido no corpo da requisição.
     * @return ResponseEntity com status 201 (Created), o cabeçalho Location da nova conta e o objeto criado no corpo.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Conta> criarConta(@RequestBody Conta conta) {
        Conta novaConta = contaService.criarConta(conta);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novaConta.getId())
                .toUri();
        return ResponseEntity.created(location).body(novaConta);
    }

    /**
     * Busca uma conta pelo seu ID.
     * @param id O ID da conta a ser buscada.
     * @return ResponseEntity com status 200 (OK) e a conta encontrada no corpo.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Conta> buscarPorId(@PathVariable Integer id) {
        Conta conta = contaService.buscarPorId(id);
        return ResponseEntity.ok(conta);
    }

    /**
     * Lista todas as contas de um cliente específico.
     * A busca é feita através de um parâmetro na URL, ex: /api/v1/contas?clienteId=1
     * @param clienteId O ID do cliente para o qual as contas serão listadas.
     * @return ResponseEntity com status 200 (OK) e uma lista de contas do cliente no corpo.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<Conta>> buscarContasPorCliente(@RequestParam Integer clienteId) {
        List<Conta> contas = contaService.buscarContasPorCliente(clienteId);
        return ResponseEntity.ok(contas);
    }
}
