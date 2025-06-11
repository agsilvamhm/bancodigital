package com.agsilvamhm.bancodigital.repository;

import com.agsilvamhm.bancodigital.model.Cliente;
import com.agsilvamhm.bancodigital.model.Role;
import com.agsilvamhm.bancodigital.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findById(Long roleId);
}
