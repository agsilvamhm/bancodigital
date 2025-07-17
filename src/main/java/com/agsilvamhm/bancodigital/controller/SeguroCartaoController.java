package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.model.SeguroCartao;
import com.agsilvamhm.bancodigital.model.dto.ContratarSeguroRequest;
import com.agsilvamhm.bancodigital.service.SeguroCartaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/seguros")
public class SeguroCartaoController {

    private final SeguroCartaoService seguroCartaoService;

    @Autowired
    public SeguroCartaoController(SeguroCartaoService seguroCartaoService) {
        this.seguroCartaoService = seguroCartaoService;
    }

    @PostMapping
    @PreAuthorize("@authService.podeAcessarCartao(#request.cartaoId())")
    public ResponseEntity<SeguroCartao> contratarSeguro(@Valid @RequestBody ContratarSeguroRequest request) {
        SeguroCartao novoSeguro = seguroCartaoService.contratarSeguro(
                request.cartaoId(),
                request.valorPremio(),
                request.cobertura(),
                request.condicoes()
        );
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoSeguro.getId())
                .toUri();
        return ResponseEntity.created(location).body(novoSeguro);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @authService.podeAcessarSeguro(#id)")
    public ResponseEntity<SeguroCartao> buscarPorId(@PathVariable Integer id) {
        SeguroCartao seguro = seguroCartaoService.buscarPorId(id);
        return ResponseEntity.ok(seguro);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<SeguroCartao>> listarTodos() {
        List<SeguroCartao> seguros = seguroCartaoService.listarTodos();
        return ResponseEntity.ok(seguros);
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @authService.podeAcessarSeguro(#id)")
    public ResponseEntity<Void> cancelarSeguro(@PathVariable Integer id) {
        seguroCartaoService.cancelarSeguro(id);
        return ResponseEntity.noContent().build();
    }
}

