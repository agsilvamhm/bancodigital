package com.agsilvamhm.bancodigital.dao;

import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.entity.CartaoCredito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CartaoCreditoDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_CARTAO = "INSERT INTO cartao_credito (id_conta, numero, validade, cvv, limite_credito, possui_seguro_viagem) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID = "SELECT * FROM cartao_credito WHERE id = ?";
    private static final String SELECT_ALL = "SELECT * FROM cartao_credito";
    private static final String UPDATE_FATURA = "UPDATE cartao_credito SET fatura_atual = ? WHERE id = ?";
    private static final String UPDATE_SEGURO_VIAGEM = "UPDATE cartao_credito SET possui_seguro_viagem = ? WHERE id = ?";

    @Autowired
    public CartaoCreditoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void salvar(CartaoCredito cartao) {
        try {
            jdbcTemplate.update(INSERT_CARTAO,
                    cartao.getConta().getId(),
                    cartao.getNumero(),
                    cartao.getValidade(),
                    cartao.getCvv(),
                    cartao.getLimiteCredito(),
                    cartao.isPossuiSeguroViagem());
        } catch (DataAccessException e) {
            throw new RepositorioException("Erro ao salvar cartão de crédito.", e);
        }
    }

    public Optional<CartaoCredito> buscarPorId(Integer id) {
        try {
            CartaoCredito cartao = jdbcTemplate.queryForObject(SELECT_BY_ID, new CartaoCreditoRowMapper(), id);
            return Optional.ofNullable(cartao);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<CartaoCredito> buscarTodos() {
        return jdbcTemplate.query(SELECT_ALL, new CartaoCreditoRowMapper());
    }

    public void atualizarFatura(Integer cartaoId, double novaFatura) {
        jdbcTemplate.update(UPDATE_FATURA, novaFatura, cartaoId);
    }

    public void atualizarSeguroViagem(Integer cartaoId, boolean status) {
        jdbcTemplate.update(UPDATE_SEGURO_VIAGEM, status, cartaoId);
    }

    private static class CartaoCreditoRowMapper implements RowMapper<CartaoCredito> {
        @Override
        public CartaoCredito mapRow(ResultSet rs, int rowNum) throws SQLException {
            CartaoCredito cartao = new CartaoCredito();
            cartao.setId(rs.getInt("id"));
            // O objeto "Conta" associado precisaria ser carregado aqui,
            // geralmente chamando o ContaDao. Omitido por simplicidade.
            cartao.setNumero(rs.getString("numero"));
            cartao.setValidade(rs.getDate("validade").toLocalDate());
            cartao.setCvv(rs.getString("cvv"));
            cartao.setLimiteCredito(rs.getDouble("limite_credito"));
            cartao.setFaturaAtual(rs.getDouble("fatura_atual"));
            cartao.setPossuiSeguroViagem(rs.getBoolean("possui_seguro_viagem"));
            return cartao;
        }
    }
}