package com.agsilvamhm.bancodigital.repository;

import com.agsilvamhm.bancodigital.model.Cartao;
import com.agsilvamhm.bancodigital.model.Conta;
import com.agsilvamhm.bancodigital.model.TipoCartao;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class CartaoDao {

    private static final Logger logger = LoggerFactory.getLogger(CartaoDao.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CartaoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Query base para reutilização, unindo as tabelas necessárias.
    private static final String BASE_SELECT_SQL = """
            SELECT
                ca.id as cartao_id, ca.numero as cartao_numero, ca.nome_titular, ca.data_validade, ca.cvv,
                ca.senha, ca.tipo_cartao, ca.limite_credito, ca.limite_diario_debito, ca.ativo,
                co.id as conta_id, co.numero as conta_numero, co.agencia
            FROM cartao ca
            JOIN conta co ON ca.id_conta = co.id
            """;

    // RowMapper para converter o resultado da query em um objeto Cartao.
    private final RowMapper<Cartao> cartaoRowMapper = (rs, rowNum) -> {
        Cartao cartao = new Cartao();
        cartao.setId(rs.getInt("cartao_id"));
        cartao.setNumero(rs.getString("cartao_numero"));
        cartao.setNomeTitular(rs.getString("nome_titular"));
        cartao.setDataValidade(rs.getDate("data_validade").toLocalDate());
        cartao.setCvv(rs.getString("cvv"));
        cartao.setSenha(rs.getString("senha"));
        cartao.setTipoCartao(TipoCartao.valueOf(rs.getString("tipo_cartao")));
        cartao.setLimiteCredito(rs.getBigDecimal("limite_credito"));
        cartao.setLimiteDiarioDebito(rs.getBigDecimal("limite_diario_debito"));
        cartao.setAtivo(rs.getBoolean("ativo"));

        // Mapeia o objeto Conta aninhado
        Conta conta = new Conta() {}; // Usando classe anônima para instanciar a classe abstrata
        conta.setId(rs.getLong("conta_id"));
        conta.setNumero(rs.getString("conta_numero"));
        conta.setAgencia(rs.getString("agencia"));
        cartao.setConta(conta);

        return cartao;
    };

    @Transactional
    public Cartao salvar(Cartao cartao) {
        Objects.requireNonNull(cartao, "O objeto cartao não pode ser nulo.");
        Objects.requireNonNull(cartao.getConta(), "A conta do cartão não pode ser nula.");
        Objects.requireNonNull(cartao.getConta().getId(), "O ID da conta não pode ser nulo.");

        String sql = """
            INSERT INTO cartao (numero, nome_titular, data_validade, cvv, senha, tipo_cartao,
                                limite_credito, limite_diario_debito, ativo, id_conta)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cartao.getNumero());
            ps.setString(2, cartao.getNomeTitular());
            ps.setDate(3, java.sql.Date.valueOf(cartao.getDataValidade()));
            ps.setString(4, cartao.getCvv());
            ps.setString(5, cartao.getSenha()); // Lembre-se de usar um hash
            ps.setString(6, cartao.getTipoCartao().name());
            ps.setBigDecimal(7, cartao.getLimiteCredito());
            ps.setBigDecimal(8, cartao.getLimiteDiarioDebito());
            ps.setBoolean(9, cartao.isAtivo());
            ps.setLong(10, cartao.getConta().getId());
            return ps;
        }, keyHolder);

        int generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        cartao.setId(generatedId);
        logger.info("Cartão salvo com ID: {}", generatedId);
        return cartao;
    }

    public Optional<Cartao> buscarPorId(Integer id) {
        String sql = BASE_SELECT_SQL + " WHERE ca.id = ?";
        try {
            Cartao cartao = jdbcTemplate.queryForObject(sql, cartaoRowMapper, id);
            return Optional.ofNullable(cartao);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Nenhum cartão encontrado com ID: {}", id);
            return Optional.empty();
        }
    }

    public List<Cartao> buscarPorContaId(Integer contaId) {
        String sql = BASE_SELECT_SQL + " WHERE ca.id_conta = ?";
        return jdbcTemplate.query(sql, cartaoRowMapper, contaId);
    }

    @Transactional
    public int atualizarStatus(Integer id, boolean ativo) {
        String sql = "UPDATE cartao SET ativo = ? WHERE id = ?";
        return jdbcTemplate.update(sql, ativo, id);
    }

    @Transactional
    public int atualizarSenha(Integer id, String novaSenhaHasheada) {
        String sql = "UPDATE cartao SET senha = ? WHERE id = ?";
        return jdbcTemplate.update(sql, novaSenhaHasheada, id);
    }

    @Transactional
    public int atualizarLimiteCredito(Integer id, java.math.BigDecimal novoLimite) {
        String sql = "UPDATE cartao SET limite_credito = ? WHERE id = ?";
        return jdbcTemplate.update(sql, novoLimite, id);
    }

    @Transactional
    public int atualizarLimiteDiarioDebito(Integer id, java.math.BigDecimal novoLimite) {
        String sql = "UPDATE cartao SET limite_diario_debito = ? WHERE id = ?";
        return jdbcTemplate.update(sql, novoLimite, id);
    }
}
