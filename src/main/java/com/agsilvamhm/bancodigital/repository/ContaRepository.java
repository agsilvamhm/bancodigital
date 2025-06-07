package com.agsilvamhm.bancodigital.repository;

import com.agsilvamhm.bancodigital.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository  extends JpaRepository<Conta, Long> {
}
