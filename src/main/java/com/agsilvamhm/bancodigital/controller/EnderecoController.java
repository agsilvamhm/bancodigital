package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.model.Endereco;
import com.agsilvamhm.bancodigital.service.EnderecoService;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enderecos")
@Validated // Habilita a validação para os parâmetros do controlador
public class EnderecoController {

    private static final Logger logger = LoggerFactory.getLogger(EnderecoController.class);

    private final EnderecoService enderecoService;

    @Autowired
    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @GetMapping("/cep/{cep}")
    @PreAuthorize("isAuthenticated()") // Exemplo de segurança: apenas usuários autenticados podem acessar
    public ResponseEntity<Endereco> buscarEnderecoPorCep(
            @PathVariable
            @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "Formato de CEP inválido. Utilize o formato xxxxx-xxx ou xxxxxxxxx.")
            String cep) {
        logger.info("Requisição para buscar endereço pelo CEP: {}", cep);
        Endereco endereco = enderecoService.buscarOuSalvarEnderecoPorCep(cep);
        return ResponseEntity.ok(endereco);
    }
}
