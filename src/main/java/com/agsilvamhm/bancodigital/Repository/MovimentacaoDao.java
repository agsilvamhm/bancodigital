package com.agsilvamhm.bancodigital.Repository;

import com.agsilvamhm.bancodigital.model.Movimentacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
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

        // O ID da conta de origem não pode ser nulo em uma transferência
        Long idOrigem = movimentacao.getContaOrigem().getId();
        // O ID da conta de destino também não
        Long idDestino = movimentacao.getContaDestino().getId();

        jdbcTemplate.update(INSERT_MOVIMENTACAO,
                movimentacao.getTipo().name(),
                movimentacao.getValor(),
                Timestamp.valueOf(movimentacao.getDataHora()),
                idOrigem,
                idDestino,
                movimentacao.getDescricao()
        );
        logger.info("Movimentação do tipo {} no valor de {} da conta ID {} para a conta ID {} salva com sucesso.",
                movimentacao.getTipo(), movimentacao.getValor(), idOrigem, idDestino);
    }
}