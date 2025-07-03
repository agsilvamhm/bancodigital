package com.agsilvamhm.bancodigital.old_dao;

import com.agsilvamhm.bancodigital.old_entity.Cartao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CartaoDao {
    private final JdbcTemplate jdbcTemplate;

    public CartaoDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public Integer salvar(Cartao cartao) { /* Implementação de insert */ return 1; }
    public void atualizar(Cartao cartao) { /* Implementação de update */ }
    public Optional<Cartao> buscarPorId(Integer id) { /* Implementação de select por ID */ return Optional.empty(); }
    public List<Cartao> buscarPorConta(Integer idConta) { /* Implementação de select por ID da conta */ return new ArrayList<>(); }
}