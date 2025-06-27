package com.agsilvamhm.bancodigital.dao;


import com.agsilvamhm.bancodigital.entity.SeguroCartao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SeguroCartaoDao {
    private final JdbcTemplate jdbcTemplate;

    public SeguroCartaoDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public Integer salvar(SeguroCartao seguro) { /* Implementação de insert */ return 1; }
    public Optional<SeguroCartao> buscarPorCartaoId(Integer idCartao) { /* Implementação de select por ID do cartão */ return Optional.empty(); }
}