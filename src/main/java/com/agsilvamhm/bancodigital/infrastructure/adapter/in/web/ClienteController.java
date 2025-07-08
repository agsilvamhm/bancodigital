package com.agsilvamhm.bancodigital.infrastructure.adapter.in.web;

import com.agsilvamhm.bancodigital.core.domain.model.Cliente;
import com.agsilvamhm.bancodigital.core.port.in.ClienteUseCase;
import com.agsilvamhm.bancodigital.infrastructure.adapter.in.web.dto.ClienteRequest;
import com.agsilvamhm.bancodigital.infrastructure.adapter.in.web.dto.ClienteResponse;
import com.agsilvamhm.bancodigital.infrastructure.adapter.in.web.mapper.ClienteMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/clientes")
public class ClienteController {

    private final ClienteUseCase clienteUseCase;
    private final ClienteMapper clienteMapper;

    public ClienteController(ClienteUseCase clienteUseCase, ClienteMapper clienteMapper) {
        this.clienteUseCase = clienteUseCase;
        this.clienteMapper = clienteMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ClienteResponse> criarCliente(@Valid @RequestBody ClienteRequest request) {
        Cliente clienteDomain = clienteMapper.toDomain(request);
        Cliente novoCliente = clienteUseCase.criarCliente(clienteDomain);
        ClienteResponse response = clienteMapper.toResponse(novoCliente);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Integer id) {
        Cliente cliente = clienteUseCase.buscarPorId(id);
        return ResponseEntity.ok(clienteMapper.toResponse(cliente));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<ClienteResponse>> listarTodos() {
        List<Cliente> clientes = clienteUseCase.listarTodos();
        List<ClienteResponse> response = clientes.stream()
                .map(clienteMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClienteResponse> atualizarCliente(@PathVariable Integer id, @Valid @RequestBody ClienteRequest request) {
        Cliente clienteParaAtualizar = clienteMapper.toDomain(request);
        Cliente clienteAtualizado = clienteUseCase.atualizarCliente(id, clienteParaAtualizar);
        return ResponseEntity.ok(clienteMapper.toResponse(clienteAtualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deletarPorId(@PathVariable Integer id) {
        clienteUseCase.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
