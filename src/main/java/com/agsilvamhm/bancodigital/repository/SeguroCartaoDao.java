package com.agsilvamhm.bancodigital.repository;

import com.agsilvamhm.bancodigital.model.Cartao;
import com.agsilvamhm.bancodigital.model.SeguroCartao;
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
public class SeguroCartaoDao {

    private static final Logger logger = LoggerFactory.getLogger(SeguroCartaoDao.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SeguroCartaoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String BASE_SELECT_SQL = """
            SELECT
                s.id as seguro_id, s.numero_apolice, s.data_contratacao, s.cobertura,
                s.condicoes, s.valor_premio,
                c.id as cartao_id, c.numero as cartao_numero, c.tipo_cartao
            FROM seguro_cartao s
            JOIN cartao c ON s.id_cartao = c.id
            """;

    private final RowMapper<SeguroCartao> seguroCartaoRowMapper = (rs, rowNum) -> {
        SeguroCartao seguro = new SeguroCartao();
        seguro.setId(rs.getInt("seguro_id"));
        seguro.setNumeroApolice(rs.getString("numero_apolice"));
        seguro.setDataContratacao(rs.getTimestamp("data_contratacao").toLocalDateTime());
        seguro.setCobertura(rs.getString("cobertura"));
        seguro.setCondicoes(rs.getString("condicoes"));
        seguro.setValorPremio(rs.getBigDecimal("valor_premio"));

        // Mapeia o objeto Cartao aninhado
        Cartao cartao = new Cartao();
        cartao.setId(rs.getInt("cartao_id"));
        cartao.setNumero(rs.getString("cartao_numero"));
        cartao.setTipoCartao(TipoCartao.valueOf(rs.getString("tipo_cartao")));
        seguro.setCartao(cartao);

        return seguro;
    };

    @Transactional
    public SeguroCartao salvar(SeguroCartao seguro) {
        Objects.requireNonNull(seguro, "O objeto seguro não pode ser nulo.");
        Objects.requireNonNull(seguro.getCartao(), "O cartão do seguro não pode ser nulo.");
        Objects.requireNonNull(seguro.getCartao().getId(), "O ID do cartão não pode ser nulo.");

        String sql = """
            INSERT INTO seguro_cartao (numero_apolice, data_contratacao, cobertura, condicoes, valor_premio, id_cartao)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, seguro.getNumeroApolice());
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(seguro.getDataContratacao()));
            ps.setString(3, seguro.getCobertura());
            ps.setString(4, seguro.getCondicoes());
            ps.setBigDecimal(5, seguro.getValorPremio());
            ps.setInt(6, seguro.getCartao().getId());
            return ps;
        }, keyHolder);

        int generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        seguro.setId(generatedId);
        logger.info("Seguro de cartão salvo com ID: {}", generatedId);
        return seguro;
    }

    public Optional<SeguroCartao> buscarPorId(Integer id) {
        String sql = BASE_SELECT_SQL + " WHERE s.id = ?";
        try {
            SeguroCartao seguro = jdbcTemplate.queryForObject(sql, seguroCartaoRowMapper, id);
            return Optional.ofNullable(seguro);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Nenhum seguro encontrado com ID: {}", id);
            return Optional.empty();
        }
    }

    public Optional<SeguroCartao> buscarPorCartaoId(Integer cartaoId) {
        String sql = BASE_SELECT_SQL + " WHERE s.id_cartao = ?";
        try {
            SeguroCartao seguro = jdbcTemplate.queryForObject(sql, seguroCartaoRowMapper, cartaoId);
            return Optional.ofNullable(seguro);
        } catch (EmptyResultDataAccessException e) {
            // É normal um cartão não ter seguro, então podemos usar um log mais ameno.
            logger.info("Nenhum seguro encontrado para o cartão de ID: {}", cartaoId);
            return Optional.empty();
        }
    }

    public List<SeguroCartao> listarTodos() {
        return jdbcTemplate.query(BASE_SELECT_SQL, seguroCartaoRowMapper);
    }

    @Transactional
    public int cancelar(Integer seguroId) {
        String sql = "DELETE FROM seguro_cartao WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, seguroId);
        if (rowsAffected > 0) {
            logger.info("Seguro com ID {} cancelado com sucesso.", seguroId);
        } else {
            logger.warn("Nenhum seguro encontrado com ID {} para cancelar.", seguroId);
        }
        return rowsAffected;
    }
}

