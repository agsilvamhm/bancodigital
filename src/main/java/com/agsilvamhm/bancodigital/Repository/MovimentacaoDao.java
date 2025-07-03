package com.agsilvamhm.bancodigital.Repository;

import com.agsilvamhm.bancodigital.model.Movimentacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Objects;

@Repository
public class MovimentacaoDao {

    private static final Logger logger = LoggerFactory.getLogger(MovimentacaoDao.class);
    private final JdbcTemplate jdbcTemplate;

    public MovimentacaoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_MOVIMENTACAO = """
        INSERT INTO movimentacao (tipo, valor, data_hora, id_conta_origem, id_conta_destino, descricao)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

    public void salvar(Movimentacao movimentacao) {
        Objects.requireNonNull(movimentacao, "Objeto de movimentação não pode ser nulo.");

        // CORREÇÃO: Trata os casos onde a conta de origem ou destino podem ser nulas.
        // Usamos um operador ternário para isso.
        Long idOrigem = (movimentacao.getContaOrigem() != null) ? movimentacao.getContaOrigem().getId() : null;
        Long idDestino = (movimentacao.getContaDestino() != null) ? movimentacao.getContaDestino().getId() : null;

        // Ao usar o jdbcTemplate.update, é uma boa prática especificar os tipos SQL,
        // especialmente para valores que podem ser nulos.
        Object[] params = {
                movimentacao.getTipo().name(),
                movimentacao.getValor(),
                Timestamp.valueOf(movimentacao.getDataHora()),
                idOrigem,
                idDestino,
                movimentacao.getDescricao()
        };

        int[] types = {
                Types.VARCHAR,
                Types.DECIMAL,
                Types.TIMESTAMP,
                Types.BIGINT, // id_conta_origem
                Types.BIGINT, // id_conta_destino
                Types.VARCHAR
        };

        jdbcTemplate.update(INSERT_MOVIMENTACAO, params, types);

        logger.info("Movimentação do tipo {} no valor de {} salva com sucesso.",
                movimentacao.getTipo(), movimentacao.getValor());
    }
}