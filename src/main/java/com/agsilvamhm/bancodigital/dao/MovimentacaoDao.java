package com.agsilvamhm.bancodigital.dao;

import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.entity.Movimentacao;
import com.agsilvamhm.bancodigital.entity.TipoMovimentacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Repository
public class MovimentacaoDao {

    private static final Logger logger = LoggerFactory.getLogger(MovimentacaoDao.class);

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_MOVIMENTACAO =
            "INSERT INTO movimentacao (tipo, valor, data_hora, id_conta_origem, id_conta_destino, descricao) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_CONTA_ID =
            "SELECT id, tipo, valor, data_hora, id_conta_origem, id_conta_destino, descricao " +
                    "FROM movimentacao WHERE id_conta_origem = ? OR id_conta_destino = ? ORDER BY data_hora DESC";

    @Autowired
    public MovimentacaoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Salva uma movimentação financeira no banco de dados.
     * @param movimentacao O objeto Movimentacao a ser salvo.
     * @return O ID da movimentação gerado.
     */
    public Integer salvar(Movimentacao movimentacao) {
        Objects.requireNonNull(movimentacao, "O objeto movimentacao não pode ser nulo.");
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_MOVIMENTACAO, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, movimentacao.getTipo().name());
                ps.setDouble(2, movimentacao.getValor());
                ps.setTimestamp(3, Timestamp.valueOf(movimentacao.getDataHora()));

                // Define os IDs de conta, que podem ser nulos (ex: depósito inicial)
                if (movimentacao.getContaOrigem() != null) {
                    ps.setInt(4, movimentacao.getContaOrigem().getId());
                } else {
                    ps.setNull(4, java.sql.Types.INTEGER);
                }

                if (movimentacao.getContaDestino() != null) {
                    ps.setInt(5, movimentacao.getContaDestino().getId());
                } else {
                    ps.setNull(5, java.sql.Types.INTEGER);
                }

                ps.setString(6, movimentacao.getDescricao());
                return ps;
            }, keyHolder);

            Integer generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
            logger.info("Movimentação ID {} salva com sucesso.", generatedId);
            return generatedId;

        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao salvar movimentação.", ex);
            throw new RepositorioException("Erro ao salvar movimentação no banco de dados.", ex);
        }
    }

    /**
     * Busca todas as movimentações de uma determinada conta, seja como origem ou destino.
     * Retorna uma lista de movimentações para compor um extrato.
     * NOTA: Este método não preenche os objetos aninhados `Conta` dentro de `Movimentacao`
     * para evitar complexidade e chamadas extras ao banco (problema N+1).
     * A camada de serviço pode, se necessário, usar os IDs para buscar os detalhes da conta.
     *
     * @param idConta O ID da conta para a qual o extrato será gerado.
     * @return Uma lista de movimentações ordenada pela data mais recente.
     */
    public List<Movimentacao> buscarPorConta(Integer idConta) {
        try {
            // Usamos um RowMapper customizado simplificado para lidar com o enum.
            return jdbcTemplate.query(SELECT_BY_CONTA_ID, (rs, rowNum) -> {
                Movimentacao mov = new Movimentacao();
                mov.setId(rs.getInt("id"));
                mov.setTipo(TipoMovimentacao.valueOf(rs.getString("tipo")));
                mov.setValor(rs.getDouble("valor"));
                mov.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());

                // Nota: Apenas os IDs são preenchidos.
                // mov.setIdContaOrigem(rs.getObject("id_conta_origem", Integer.class));
                // mov.setIdContaDestino(rs.getObject("id_conta_destino", Integer.class));

                mov.setDescricao(rs.getString("descricao"));
                return mov;
            }, idConta, idConta);
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao buscar movimentações da conta ID: {}", idConta, ex);
            throw new RepositorioException("Erro ao buscar extrato da conta.", ex);
        }
    }
}

