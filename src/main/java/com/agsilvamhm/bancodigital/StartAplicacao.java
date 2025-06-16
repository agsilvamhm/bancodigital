package com.agsilvamhm.bancodigital;

import com.agsilvamhm.bancodigital.autenticacao.model.Role;
import com.agsilvamhm.bancodigital.autenticacao.model.User;
import com.agsilvamhm.bancodigital.autenticacao.repository.RoleRepository;
import com.agsilvamhm.bancodigital.autenticacao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class StartAplicacao implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);

        User user1 = new User();
        user1.setUsername("Adalberto");
        user1.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user1);

        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);

        Role role1 = new Role();
        role1.setName("CLIENTE");
        roleRepository.save(role1);

        user.setRoles(Set.of(role));
        user1.setRoles(Set.of(role1));
        userRepository.save(user);
        userRepository.save(user1);
    }
}
