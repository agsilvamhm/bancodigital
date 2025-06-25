package com.agsilvamhm.bancodigital.dao;

import com.agsilvamhm.bancodigital.controller.exception.ContaDuplicadaException;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.entity.ContaCorrente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * DAO para a entidade ContaCorrente.
 * Gerencia as operações de persistência nas tabelas 'conta' e 'conta_corrente'.
 * IMPORTANTE: Os métodos que alteram dados (salvar, atualizar) devem ser
 * executados dentro de um contexto transacional (@Transactional) no nível de serviço
 * para garantir a atomicidade das operações em ambas as tabelas.
 */
@Repository
public class ContaCorrenteDao {

    private static final Logger logger = LoggerFactory.getLogger(ContaCorrenteDao.class);

    // --- QUERIES SQL ---
    private static final String INSERT_CONTA = "INSERT INTO conta (id_cliente, agencia, numero_conta, saldo) VALUES (?, ?, ?, ?)";
    private static final String INSERT_CONTA_CORRENTE = "INSERT INTO conta_corrente (conta_id, taxa_manutencao) VALUES (?, ?)";

    private static final String SELECT_CC_BY_ID = "SELECT c.*, cc.taxa_manutencao " +
            "FROM conta c " +
            "JOIN conta_corrente cc ON c.id = cc.conta_id " +
            "WHERE c.id = ?";

    private static final String SELECT_ALL_CC = "SELECT c.*, cc.taxa_manutencao " +
            "FROM conta c " +
            "JOIN conta_corrente cc ON c.id = cc.conta_id";

    private static final String UPDATE_CONTA = "UPDATE conta SET id_cliente = ?, agencia = ?, numero_conta = ?, saldo = ? WHERE id = ?";
    private static final String UPDATE_CONTA_CORRENTE = "UPDATE conta_corrente SET taxa_manutencao = ? WHERE conta_id = ?";

    private static final String DELETE_CONTA_BY_ID = "DELETE FROM conta WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    public ContaCorrenteDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Salva uma nova conta corrente, inserindo dados nas tabelas 'conta' e 'conta_corrente'.
     *
     * @param conta O objeto ContaCorrente a ser salvo.
     * @return O objeto ContaCorrente com o ID gerado.
     */
    public ContaCorrente salvar(ContaCorrente conta) {
        Objects.requireNonNull(conta, "O objeto conta corrente não pode ser nulo.");
        try {
            // 1. Inserir na tabela 'conta' e obter o ID gerado
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_CONTA, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, conta.getIdCliente());
                ps.setString(2, conta.getAgencia());
                ps.setString(3, conta.getNumeroConta());
                ps.setBigDecimal(4, conta.getSaldo());
                return ps;
            }, keyHolder);

            int contaId = Objects.requireNonNull(keyHolder.getKey()).intValue();
            conta.setId(contaId);

            // 2. Inserir na tabela 'conta_corrente' usando o ID obtido
            jdbcTemplate.update(INSERT_CONTA_CORRENTE, contaId, conta.getTaxaManutencao());

            logger.info("Conta Corrente salva com sucesso: ID {}", contaId);
            return conta;
        } catch (DuplicateKeyException ex) {
            String erroMsg = String.format("Erro ao salvar conta: Agência/Número (%s/%s) já cadastrado.", conta.getAgencia(), conta.getNumeroConta());
            logger.error(erroMsg, ex);
            throw new ContaDuplicadaException(erroMsg);
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao salvar conta corrente.", ex);
            throw new RepositorioException("Erro ao salvar conta corrente no banco de dados.", ex);
        }
    }

    /**
     * Busca uma Conta Corrente completa pelo seu ID.
     *
     * @param id O ID da conta.
     * @return Um Optional contendo a ContaCorrente, ou vazio se não encontrada.
     */
    public Optional<ContaCorrente> buscarPorId(Integer id) {
        try {
            ContaCorrente conta = jdbcTemplate.queryForObject(SELECT_CC_BY_ID, new ContaCorrenteRowMapper(), id);
            return Optional.ofNullable(conta);
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("Nenhuma conta corrente encontrada com o ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao buscar conta corrente pelo ID: {}", id, ex);
            throw new RepositorioException("Erro ao buscar conta corrente por ID.", ex);
        }
    }

    /**
     * Retorna uma lista de todas as contas correntes.
     *
     * @return Uma lista de objetos ContaCorrente.
     */
    public List<ContaCorrente> listarTodos() {
        try {
            return jdbcTemplate.query(SELECT_ALL_CC, new ContaCorrenteRowMapper());
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao listar todas as contas correntes.", ex);
            throw new RepositorioException("Erro ao listar todas as contas correntes.", ex);
        }
    }

    /**
     * Atualiza os dados de uma conta corrente nas tabelas 'conta' e 'conta_corrente'.
     *
     * @param conta O objeto ContaCorrente com os dados atualizados.
     */
    public void atualizar(ContaCorrente conta) {
        Objects.requireNonNull(conta, "O objeto conta corrente não pode ser nulo.");
        Objects.requireNonNull(conta.getId(), "O ID da conta não pode ser nulo para atualização.");
        try {
            // 1. Atualiza a tabela 'conta'
            int linhasAfetadas = jdbcTemplate.update(UPDATE_CONTA,
                    conta.getIdCliente(),
                    conta.getAgencia(),
                    conta.getNumeroConta(),
                    conta.getSaldo(),
                    conta.getId());

            if (linhasAfetadas == 0) {
                throw new EntidadeNaoEncontradaException("Conta com ID " + conta.getId() + " não encontrada para atualização.");
            }

            // 2. Atualiza a tabela 'conta_corrente'
            jdbcTemplate.update(UPDATE_CONTA_CORRENTE, conta.getTaxaManutencao(), conta.getId());

            logger.info("Conta Corrente atualizada com sucesso: ID {}", conta.getId());
        } catch (DuplicateKeyException ex) {
            String erroMsg = String.format("Erro ao atualizar conta: Agência/Número (%s/%s) já pertence a outra conta.", conta.getAgencia(), conta.getNumeroConta());
            logger.error(erroMsg, ex);
            throw new ContaDuplicadaException(erroMsg);
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao tentar atualizar a conta corrente: {}", conta.getId(), ex);
            throw new RepositorioException("Erro ao atualizar conta corrente.", ex);
        }
    }

    /**
     * Deleta uma conta pelo seu ID. A constraint ON DELETE CASCADE irá remover a entrada
     * correspondente da tabela 'conta_corrente'.
     *
     * @param id O ID da conta a ser deletada.
     */
    public void deletar(Integer id) {
        Objects.requireNonNull(id, "O ID para deleção não pode ser nulo.");
        try {
            int linhasAfetadas = jdbcTemplate.update(DELETE_CONTA_BY_ID, id);
            if (linhasAfetadas == 0) {
                throw new EntidadeNaoEncontradaException("Conta com ID " + id + " não encontrada para deleção.");
            }
            logger.info("Conta (e Conta Corrente correspondente) deletada com sucesso: ID {}", id);
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao deletar conta pelo ID: {}", id, ex);
            throw new RepositorioException("Erro ao deletar conta.", ex);
        }
    }

    /**
     * RowMapper customizado para mapear o resultado do JOIN para um objeto ContaCorrente.
     */
    private static class ContaCorrenteRowMapper implements RowMapper<ContaCorrente> {
        @Override
        public ContaCorrente mapRow(ResultSet rs, int rowNum) throws SQLException {
            ContaCorrente conta = new ContaCorrente();
            // Mapeia campos da tabela 'conta'
            conta.setId(rs.getInt("id"));
            conta.setIdCliente(rs.getInt("id_cliente"));
            conta.setAgencia(rs.getString("agencia"));
            conta.setNumeroConta(rs.getString("numero_conta"));
            conta.setSaldo(rs.getBigDecimal("saldo"));
            conta.setDataAbertura(rs.getTimestamp("data_abertura").toLocalDateTime());
            // Mapeia campo da tabela 'conta_corrente'
            conta.setTaxaManutencao(rs.getBigDecimal("taxa_manutencao"));
            return conta;
        }
    }
}

