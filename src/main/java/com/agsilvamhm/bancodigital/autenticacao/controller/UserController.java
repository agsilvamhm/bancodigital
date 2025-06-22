package com.agsilvamhm.bancodigital.autenticacao.controller;

import com.agsilvamhm.bancodigital.autenticacao.model.dto.CreateUserDto;
import com.agsilvamhm.bancodigital.autenticacao.model.dto.UsuarioDto;

import com.agsilvamhm.bancodigital.autenticacao.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuários Controller", description = "RESTful API para controle de usuários.")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<UsuarioDto> newUser(@RequestBody CreateUserDto dto, JwtAuthenticationToken token) {
        var user = userService.create(dto, token);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getUserId())
                .toUri();

        return ResponseEntity.created(location)
                .body(new UsuarioDto(user.getUsername(), user.getRoles().iterator().next().getName()));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Operation(summary = "Lista os usuários cadastrados", description = "Lista todos os usuários cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação bem sucedida"),
            @ApiResponse(responseCode = "401", description = "Autorização não permitida")})
    public ResponseEntity<List<UsuarioDto>> listUsers(){
        var users = userService.listaUsuarios();
        return ResponseEntity.ok(users);
    }
}
