package com.agsilvamhm.bancodigital.autenticacao.service;

import com.agsilvamhm.bancodigital.autenticacao.model.Role;
import com.agsilvamhm.bancodigital.autenticacao.model.User;
import com.agsilvamhm.bancodigital.autenticacao.model.dto.CreateUserDto;
import com.agsilvamhm.bancodigital.autenticacao.model.dto.UsuarioDto;
import com.agsilvamhm.bancodigital.autenticacao.repository.RoleRepository;
import com.agsilvamhm.bancodigital.autenticacao.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder
                       ){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Transactional
    public User create(CreateUserDto dto, JwtAuthenticationToken token) {
        var userLogado = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));

        boolean isAdmin = userLogado.getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        var basicRole = roleRepository.findByName(Role.Values.CLIENTE.name());
        if (basicRole == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role padrão não encontrada");
        }

        if (userRepository.findByUsername(dto.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Usuário já existe");
        }

        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas administradores podem criar usuários");
        }

        var user = new User();
        user.setUsername(dto.username());
        user.setPassword(bCryptPasswordEncoder.encode(dto.password()));
        user.setRoles(Set.of(basicRole));

        return userRepository.save(user);
    }



    public List<UsuarioDto> listaUsuarios() {
        List<User> usuarios = userRepository.findAll();
        List<UsuarioDto> usuariosDto = usuarios.stream()
                .map(usuario -> {
                    List<String> roleNames = usuario.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toList());
                    return new UsuarioDto(usuario.getUsername(), roleNames.toString());
                })
                .collect(Collectors.toList());
        return usuariosDto;
    }
}