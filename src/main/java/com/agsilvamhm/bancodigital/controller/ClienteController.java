package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.entity.Cliente;
import com.agsilvamhm.bancodigital.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="clientes")
public class ClienteController {

    private ClienteService clienteService;

    public ClienteController(ClienteService clienteService){
        this.clienteService = clienteService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> newCliente(@RequestBody Cliente cliente, JwtAuthenticationToken token){
        clienteService.criar(cliente, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Integer id) {
         var cliente = clienteService.getClienteId(id);
         return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> newCliente(Cliente cliente){
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Cliente> delClienteById(@PathVariable Integer id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/listall")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<Cliente>> getClienteAll() {
        var clientes = clienteService.listarClientes();
        return ResponseEntity.ok(clientes);
    }
}
