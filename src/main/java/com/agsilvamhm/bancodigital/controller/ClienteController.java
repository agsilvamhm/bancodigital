package com.agsilvamhm.bancodigital.controller;

import com.agsilvamhm.bancodigital.model.Cliente;
import com.agsilvamhm.bancodigital.model.dto.ClienteDto;
import com.agsilvamhm.bancodigital.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/adicionar")
    public ResponseEntity<Void> newCliente(@RequestBody ClienteDto dto){

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Integer id) {
        Cliente cliente = clienteService.findById(id);
         return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> newCliente(){
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Cliente> delClienteById(@PathVariable Integer id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/lista")
    public ResponseEntity<Cliente> getClienteAll() {
      //  Cliente cliente = clienteService.findByAll();
        return ResponseEntity.ok().build();
    }


}
