package com.agsilvamhm.bancodigital.repository;

import com.agsilvamhm.bancodigital.model.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepository extends JpaRepository<Cartao, Long> {
}
