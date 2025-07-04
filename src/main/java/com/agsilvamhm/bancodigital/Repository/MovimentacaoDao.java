package com.agsilvamhm.bancodigital.Repository;

import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.model.Conta;
import com.agsilvamhm.bancodigital.model.ContaCorrente;
import com.agsilvamhm.bancodigital.model.Movimentacao;
import com.agsilvamhm.bancodigital.model.TipoMovimentacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
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

    // Importações necessárias no início do arquivo MovimentacaoDao.java

    // 1. CRIE UM ROW MAPPER PARA A ENTIDADE MOVIMENTACAO
// Este mapper irá converter o resultado da consulta SQL em objetos Movimentacao.
    private final RowMapper<Movimentacao> movimentacaoRowMapper = (rs, rowNum) -> {
        Movimentacao mov = new Movimentacao();
        mov.setId(rs.getInt("mov_id"));
        mov.setTipo(TipoMovimentacao.valueOf(rs.getString("tipo")));
        mov.setValor(rs.getDouble("valor"));
        // Importante: Converte o Timestamp do banco para LocalDateTime
        mov.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        mov.setDescricao(rs.getString("descricao"));

        // Mapeia a conta de origem, se existir
        long idOrigem = rs.getLong("id_conta_origem");
        if (!rs.wasNull()) {
            Conta contaOrigem = new ContaCorrente(); // Usamos uma instância genérica, pois só precisamos do ID e número
            contaOrigem.setId(idOrigem);
            contaOrigem.setNumero(rs.getString("num_conta_origem"));
            mov.setContaOrigem(contaOrigem);
        }

        // Mapeia a conta de destino, se existir
        long idDestino = rs.getLong("id_conta_destino");
        if (!rs.wasNull()) {
            Conta contaDestino = new ContaCorrente(); // Instância genérica
            contaDestino.setId(idDestino);
            contaDestino.setNumero(rs.getString("num_conta_destino"));
            mov.setContaDestino(contaDestino);
        }

        return mov;
    };

// 2. CRIE O MÉTODO PARA BUSCAR MOVIMENTAÇÕES POR ID DA CONTA
    /**
     * Busca no banco de dados uma lista de todas as movimentações (entradas e saídas)
     * associadas a um ID de conta específico.
     * Os resultados são ordenados pela data, da mais recente para a mais antiga.
     *
     * @param contaId O ID da conta para a qual o extrato será gerado.
     * @return Uma lista de objetos Movimentacao.
     */
    public List<Movimentacao> buscarPorContaId(Long contaId) {
        // A query usa LEFT JOIN para que depósitos (sem origem) e saques (sem destino) funcionem.
        // O WHERE crucial é "m.id_conta_origem = ? OR m.id_conta_destino = ?"
        final String sql = """
        SELECT
            m.id as mov_id,
            m.tipo,
            m.valor,
            m.data_hora,
            m.descricao,
            m.id_conta_origem,
            orig.numero as num_conta_origem,
            m.id_conta_destino,
            dest.numero as num_conta_destino
        FROM
            movimentacao m
        LEFT JOIN conta orig ON m.id_conta_origem = orig.id
        LEFT JOIN conta dest ON m.id_conta_destino = dest.id
        WHERE
            m.id_conta_origem = ? OR m.id_conta_destino = ?
        ORDER BY
            m.data_hora DESC
    """;

        try {
            // Passamos o ID da conta duas vezes, uma para cada placeholder (?) na query
            return jdbcTemplate.query(sql, movimentacaoRowMapper, contaId, contaId);
        } catch (DataAccessException e) {
            logger.error("Erro ao buscar movimentações para a conta ID: {}", contaId, e);
            // Em caso de erro, lançamos uma exceção de repositório para ser tratada pela camada de serviço
            throw new RepositorioException("Erro ao acessar o extrato da conta.", e);
        }
    }
}