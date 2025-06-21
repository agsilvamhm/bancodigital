package com.agsilvamhm.bancodigital.repository;

import com.agsilvamhm.bancodigital.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findById(Integer id);
}
