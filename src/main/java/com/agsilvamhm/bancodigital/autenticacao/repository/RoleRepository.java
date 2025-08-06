package com.agsilvamhm.bancodigital.autenticacao.repository;

import com.agsilvamhm.bancodigital.autenticacao.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface RoleRepository  {
    Optional<Role> findById(Long roleId);

    Role findByName(String name);
}
