package com.agsilvamhm.bancodigital.dao;

import com.agsilvamhm.bancodigital.controller.exception.ContaDuplicadaException;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.entity.ContaPoupanca;
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
 * DAO para a entidade ContaPoupanca.
 * Gerencia as operações de persistência nas tabelas 'conta' e 'conta_poupanca'.
 * IMPORTANTE: Os métodos que alteram dados (salvar, atualizar) devem ser
 * executados dentro de um contexto transacional (@Transactional) no nível de serviço
 * para garantir a atomicidade das operações em ambas as tabelas.
 */
@Repository
public class ContaPoupancaDao {

    private static final Logger logger = LoggerFactory.getLogger(ContaPoupancaDao.class);

    // --- QUERIES SQL ---
    private static final String INSERT_CONTA = "INSERT INTO conta (id_cliente, agencia, numero_conta, saldo) VALUES (?, ?, ?, ?)";
    private static final String INSERT_CONTA_POUPANCA = "INSERT INTO conta_poupanca (conta_id, taxa_rendimento) VALUES (?, ?)";

    private static final String SELECT_CP_BY_ID = "SELECT c.*, cp.taxa_rendimento " +
            "FROM conta c " +
            "JOIN conta_poupanca cp ON c.id = cp.conta_id " +
            "WHERE c.id = ?";

    private static final String SELECT_ALL_CP = "SELECT c.*, cp.taxa_rendimento " +
            "FROM conta c " +
            "JOIN conta_poupanca cp ON c.id = cp.conta_id";

    private static final String UPDATE_CONTA = "UPDATE conta SET id_cliente = ?, agencia = ?, numero_conta = ?, saldo = ? WHERE id = ?";
    private static final String UPDATE_CONTA_POUPANCA = "UPDATE conta_poupanca SET taxa_rendimento = ? WHERE conta_id = ?";

    private static final String DELETE_CONTA_BY_ID = "DELETE FROM conta WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    public ContaPoupancaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Salva uma nova conta poupança, inserindo dados nas tabelas 'conta' e 'conta_poupanca'.
     *
     * @param conta O objeto ContaPoupanca a ser salvo.
     * @return O objeto ContaPoupanca com o ID gerado.
     */
    public ContaPoupanca salvar(ContaPoupanca conta) {
        Objects.requireNonNull(conta, "O objeto conta poupança não pode ser nulo.");
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

            // 2. Inserir na tabela 'conta_poupanca' usando o ID obtido
            jdbcTemplate.update(INSERT_CONTA_POUPANCA, contaId, conta.getTaxaRendimento());

            logger.info("Conta Poupança salva com sucesso: ID {}", contaId);
            return conta;
        } catch (DuplicateKeyException ex) {
            String erroMsg = String.format("Erro ao salvar conta: Agência/Número (%s/%s) já cadastrado.", conta.getAgencia(), conta.getNumeroConta());
            logger.error(erroMsg, ex);
            throw new ContaDuplicadaException(erroMsg);
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao salvar conta poupança.", ex);
            throw new RepositorioException("Erro ao salvar conta poupança no banco de dados.", ex);
        }
    }

    /**
     * Busca uma Conta Poupança completa pelo seu ID.
     *
     * @param id O ID da conta.
     * @return Um Optional contendo a ContaPoupanca, ou vazio se não encontrada.
     */
    public Optional<ContaPoupanca> buscarPorId(Integer id) {
        try {
            ContaPoupanca conta = jdbcTemplate.queryForObject(SELECT_CP_BY_ID, new ContaPoupancaRowMapper(), id);
            return Optional.ofNullable(conta);
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("Nenhuma conta poupança encontrada com o ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao buscar conta poupança pelo ID: {}", id, ex);
            throw new RepositorioException("Erro ao buscar conta poupança por ID.", ex);
        }
    }

    /**
     * Retorna uma lista de todas as contas poupança.
     *
     * @return Uma lista de objetos ContaPoupanca.
     */
    public List<ContaPoupanca> listarTodos() {
        try {
            return jdbcTemplate.query(SELECT_ALL_CP, new ContaPoupancaRowMapper());
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao listar todas as contas poupança.", ex);
            throw new RepositorioException("Erro ao listar todas as contas poupança.", ex);
        }
    }

    /**
     * Atualiza os dados de uma conta poupança nas tabelas 'conta' e 'conta_poupanca'.
     *
     * @param conta O objeto ContaPoupanca com os dados atualizados.
     */
    public void atualizar(ContaPoupanca conta) {
        Objects.requireNonNull(conta, "O objeto conta poupança não pode ser nulo.");
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

            // 2. Atualiza a tabela 'conta_poupanca'
            jdbcTemplate.update(UPDATE_CONTA_POUPANCA, conta.getTaxaRendimento(), conta.getId());

            logger.info("Conta Poupança atualizada com sucesso: ID {}", conta.getId());
        } catch (DuplicateKeyException ex) {
            String erroMsg = String.format("Erro ao atualizar conta: Agência/Número (%s/%s) já pertence a outra conta.", conta.getAgencia(), conta.getNumeroConta());
            logger.error(erroMsg, ex);
            throw new ContaDuplicadaException(erroMsg);
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao tentar atualizar a conta poupança: {}", conta.getId(), ex);
            throw new RepositorioException("Erro ao atualizar conta poupança.", ex);
        }
    }

    /**
     * Deleta uma conta pelo seu ID. A constraint ON DELETE CASCADE irá remover a entrada
     * correspondente da tabela 'conta_poupanca'.
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
            logger.info("Conta (e Conta Poupança correspondente) deletada com sucesso: ID {}", id);
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao deletar conta pelo ID: {}", id, ex);
            throw new RepositorioException("Erro ao deletar conta.", ex);
        }
    }

    /**
     * RowMapper customizado para mapear o resultado do JOIN para um objeto ContaPoupanca.
     */
    private static class ContaPoupancaRowMapper implements RowMapper<ContaPoupanca> {
        @Override
        public ContaPoupanca mapRow(ResultSet rs, int rowNum) throws SQLException {
            ContaPoupanca conta = new ContaPoupanca();
            // Mapeia campos da tabela 'conta'
            conta.setId(rs.getInt("id"));
            conta.setIdCliente(rs.getInt("id_cliente"));
            conta.setAgencia(rs.getString("agencia"));
            conta.setNumeroConta(rs.getString("numero_conta"));
            conta.setSaldo(rs.getBigDecimal("saldo"));
            conta.setDataAbertura(rs.getTimestamp("data_abertura").toLocalDateTime());
            // Mapeia campo da tabela 'conta_poupanca'
            conta.setTaxaRendimento(rs.getBigDecimal("taxa_rendimento"));
            return conta;
        }
    }
}