package com.agsilvamhm.bancodigital.dao;

import com.agsilvamhm.bancodigital.entity.TransacaoCartao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;

@Repository
public class TransacaoCartaoDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_TRANSACAO = "INSERT INTO transacao_cartao (id_cartao_credito, valor, descricao) VALUES (?, ?, ?)";
    private static final String SUM_GASTOS_MES = "SELECT COALESCE(SUM(valor), 0) FROM transacao_cartao WHERE id_cartao_credito = ? AND YEAR(data_transacao) = ? AND MONTH(data_transacao) = ?";

    @Autowired
    public TransacaoCartaoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void salvar(TransacaoCartao transacao) {
        jdbcTemplate.update(INSERT_TRANSACAO,
                transacao.getCartaoCredito().getId(),
                transacao.getValor(),
                transacao.getDescricao());
    }

    public double somarGastosNoMes(Integer cartaoId, YearMonth mes) {
        return jdbcTemplate.queryForObject(SUM_GASTOS_MES, Double.class, cartaoId, mes.getYear(), mes.getMonthValue());
    }
}