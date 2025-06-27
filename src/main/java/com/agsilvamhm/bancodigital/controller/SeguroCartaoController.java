package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.entity.SeguroCartao;
import com.agsilvamhm.bancodigital.service.SeguroCartaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seguros-cartao")
public class SeguroCartaoController {
    private final SeguroCartaoService seguroCartaoService;

    public SeguroCartaoController(SeguroCartaoService seguroCartaoService) { this.seguroCartaoService = seguroCartaoService; }

    @PostMapping
    public ResponseEntity<SeguroCartao> contratar(@RequestBody ContratacaoRequest request) {
        return ResponseEntity.ok(seguroCartaoService.contratarSeguro(request.getIdCartao()));
    }

    @GetMapping("/cartao/{idCartao}")
    public ResponseEntity<SeguroCartao> buscarPorCartao(@PathVariable Integer idCartao) {
        return ResponseEntity.ok(seguroCartaoService.buscarSeguroPorCartao(idCartao));
    }

    // DTO
    static class ContratacaoRequest {
        private Integer idCartao;

        public Integer getIdCartao() { return idCartao; }
        public void setIdCartao(Integer idCartao) { this.idCartao = idCartao; }
    }
}
