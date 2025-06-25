package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.entity.Conta;
import com.agsilvamhm.bancodigital.entity.ContaCorrente;
import com.agsilvamhm.bancodigital.entity.ContaPoupanca;
import com.agsilvamhm.bancodigital.entity.dto.*;
import com.agsilvamhm.bancodigital.service.ContaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/contas")
public class ContaController {

    private final ContaService contaService;
    // Em um cenário real, operações transacionais poderiam estar em um serviço dedicado.
    // private final TransacaoService transacaoService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    /**
     * Endpoint para criar uma nova conta (Corrente ou Poupança).
     * A decisão do tipo é baseada no DTO de entrada.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> criarConta(@RequestBody CriarContaRequest request) {
        Conta novaConta;
        // Lógica para decidir qual serviço chamar com base no tipo
        if (request.getTipoConta() == TipoConta.CORRENTE) {
            ContaCorrente cc = new ContaCorrente(/*...mapeia dados do request...*/);
            cc.setTaxaManutencao(request.getTaxaManutencao());
            novaConta = contaService.criarContaCorrente(cc);
        } else if (request.getTipoConta() == TipoConta.POUPANCA) {
            ContaPoupanca cp = new ContaPoupanca(/*...mapeia dados do request...*/);
            cp.setTaxaRendimento(request.getTaxaRendimento());
            novaConta = contaService.criarContaPoupanca(cp);
        } else {
            return ResponseEntity.badRequest().body("Tipo de conta inválido.");
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novaConta.getId())
                .toUri();
        return ResponseEntity.created(location).body(novaConta);
    }

    /**
     * Endpoint para buscar os detalhes de qualquer conta pelo ID.
     * Retorna um DTO de resposta unificado.
     */
    @GetMapping("/{id}")
    // Um usuário pode ver sua própria conta, ou um admin pode ver qualquer uma.
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @segurancaService.pertenceAoUsuario(#id)")
    public ResponseEntity<ContaResponse> buscarPorId(@PathVariable Integer id) {
        Conta conta = contaService.buscarContaPorId(id);
        // Aqui, você mapearia o objeto 'Conta' para 'ContaResponse'
        ContaResponse response = mapearParaContaResponse(conta);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para consultar o saldo de uma conta.
     */
    @GetMapping("/{id}/saldo")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @segurancaService.pertenceAoUsuario(#id)")
    public ResponseEntity<SaldoResponse> consultarSaldo(@PathVariable Integer id) {
        Conta conta = contaService.buscarContaPorId(id);
        return ResponseEntity.ok(new SaldoResponse(conta.getSaldo()));
    }

    /**
     * Endpoint para realizar um depósito em uma conta.
     */
    @PostMapping("/{id}/deposito")
    // Geralmente, qualquer pessoa pode depositar, então pode ser menos restrito.
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> realizarDeposito(@PathVariable Integer id, @RequestBody OperacaoValorRequest request) {
        // contaService.realizarDeposito(id, request.getValor());
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para realizar um saque de uma conta.
     */
    @PostMapping("/{id}/saque")
    @PreAuthorize("@segurancaService.pertenceAoUsuario(#id)")
    public ResponseEntity<Void> realizarSaque(@PathVariable Integer id, @RequestBody OperacaoValorRequest request) {
        // contaService.realizarSaque(id, request.getValor());
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para realizar uma transferência entre contas.
     */
    @PostMapping("/{id}/transferencia")
    @PreAuthorize("@segurancaService.pertenceAoUsuario(#id)")
    public ResponseEntity<Void> realizarTransferencia(@PathVariable Integer id, @RequestBody TransferenciaRequest request) {
        // contaService.realizarTransferencia(id, request.getIdContaDestino(), request.getValor());
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para realizar um pagamento via Pix.
     */
    @PostMapping("/{id}/pix")
    @PreAuthorize("@segurancaService.pertenceAoUsuario(#id)")
    public ResponseEntity<Void> realizarPix(@PathVariable Integer id, @RequestBody PixRequest request) {
        // contaService.realizarPix(id, request.getChavePix(), request.getValor());
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para aplicar a taxa de manutenção (apenas para contas correntes).
     * Tipicamente uma operação de sistema/batch, restrita a admins.
     */
    @PutMapping("/{id}/manutencao")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> aplicarTaxaManutencao(@PathVariable Integer id) {
        // contaService.aplicarTaxaManutencao(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para aplicar rendimentos (apenas para contas poupança).
     * Tipicamente uma operação de sistema/batch, restrita a admins.
     */
    @PutMapping("/{id}/rendimentos")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> aplicarRendimentos(@PathVariable Integer id) {
        // contaService.aplicarRendimentos(id);
        return ResponseEntity.noContent().build();
    }

    // Método auxiliar para mapear Entidade para DTO
    private ContaResponse mapearParaContaResponse(Conta conta) {
        ContaResponse dto = new ContaResponse();
        dto.setId(conta.getId());
        dto.setIdCliente(conta.getIdCliente());
        dto.setAgencia(conta.getAgencia());
        dto.setNumeroConta(conta.getNumeroConta());
        dto.setSaldo(conta.getSaldo());

        if (conta instanceof ContaCorrente) {
            dto.setTipoConta("Conta Corrente");
            dto.setTaxaManutencao(((ContaCorrente) conta).getTaxaManutencao());
        } else if (conta instanceof ContaPoupanca) {
            dto.setTipoConta("Conta Poupança");
            dto.setTaxaRendimento(((ContaPoupanca) conta).getTaxaRendimento());
        }
        return dto;
    }
}
