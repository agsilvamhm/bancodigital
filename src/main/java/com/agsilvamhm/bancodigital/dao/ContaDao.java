package com.agsilvamhm.bancodigital.dao;

import com.agsilvamhm.bancodigital.controller.exception.ContaDuplicadaException;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.entity.Conta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * DAO base para a entidade Conta.
 * Gerencia as operações de persistência para a tabela 'conta'.
 * Pode ser usado para operações genéricas em contas, sem distinção de tipo.
 */
@Repository
public class ContaDao {

    private static final Logger logger = LoggerFactory.getLogger(ContaDao.class);

    // --- QUERIES SQL ---
    private static final String INSERT_CONTA = "INSERT INTO conta (id_cliente, agencia, numero_conta, saldo) VALUES (?, ?, ?, ?)";
    private static final String SELECT_BY_ID = "SELECT * FROM conta WHERE id = ?";
    private static final String SELECT_ALL = "SELECT * FROM conta";
    private static final String UPDATE_CONTA = "UPDATE conta SET id_cliente = ?, agencia = ?, numero_conta = ?, saldo = ? WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM conta WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    public ContaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Salva uma conta genérica na tabela 'conta' e retorna o objeto com o ID gerado.
     * Este método é fundamental para obter o ID da conta antes de inseri-la em tabelas especializadas.
     *
     * @param conta O objeto Conta a ser salvo. Não pode ser nulo.
     * @return A conta com o ID atribuído pelo banco de dados.
     * @throws RepositorioException se ocorrer um erro de acesso a dados.
     * @throws ContaDuplicadaException se a combinação de agência e número de conta já existir.
     */
    public Conta salvar(Conta conta) {
        Objects.requireNonNull(conta, "O objeto conta não pode ser nulo.");
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_CONTA, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, conta.getIdCliente());
                ps.setString(2, conta.getAgencia());
                ps.setString(3, conta.getNumeroConta());
                ps.setBigDecimal(4, conta.getSaldo());
                return ps;
            }, keyHolder);

            // Define o ID gerado no objeto conta
            if (keyHolder.getKey() != null) {
                conta.setId(keyHolder.getKey().intValue());
            }

            logger.info("Conta salva com sucesso: Agência {}, Número {}", conta.getAgencia(), conta.getNumeroConta());
            return conta;
        } catch (DuplicateKeyException ex) {
            String erroMsg = String.format("Erro ao salvar conta: Agência/Número (%s/%s) já cadastrado.", conta.getAgencia(), conta.getNumeroConta());
            logger.error(erroMsg, ex);
            throw new ContaDuplicadaException(erroMsg);
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao tentar salvar a conta: {}", conta.getNumeroConta(), ex);
            throw new RepositorioException("Erro ao salvar conta no banco de dados.", ex);
        }
    }

    /**
     * Busca uma conta pelo seu ID.
     *
     * @param id O ID da conta.
     * @return um Optional contendo a conta se encontrada, ou um Optional vazio.
     * @throws RepositorioException se ocorrer um erro de acesso a dados.
     */
    public Optional<Conta> buscarPorId(Integer id) {
        try {
            Conta conta = jdbcTemplate.queryForObject(SELECT_BY_ID, new BeanPropertyRowMapper<>(Conta.class), id);
            return Optional.ofNullable(conta);
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("Nenhuma conta encontrada com o ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao buscar conta pelo ID: {}", id, ex);
            throw new RepositorioException("Erro ao buscar conta por ID.", ex);
        }
    }

    /**
     * Retorna uma lista com todas as contas cadastradas.
     *
     * @return Lista de Contas.
     * @throws RepositorioException se ocorrer um erro de acesso a dados.
     */
    public List<Conta> listarTodos() {
        try {
            return jdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(Conta.class));
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao listar todas as contas.", ex);
            throw new RepositorioException("Erro ao listar todas as contas.", ex);
        }
    }

    /**
     * Atualiza os dados de uma conta existente.
     *
     * @param conta O objeto conta com os dados atualizados. O ID não pode ser nulo.
     * @throws EntidadeNaoEncontradaException se a conta com o ID especificado não existir.
     * @throws RepositorioException se ocorrer um erro de acesso a dados.
     */
    public void atualizar(Conta conta) {
        Objects.requireNonNull(conta, "O objeto conta não pode ser nulo.");
        Objects.requireNonNull(conta.getId(), "O ID da conta não pode ser nulo para atualização.");
        try {
            int linhasAfetadas = jdbcTemplate.update(UPDATE_CONTA,
                    conta.getIdCliente(),
                    conta.getAgencia(),
                    conta.getNumeroConta(),
                    conta.getSaldo(),
                    conta.getId());

            if (linhasAfetadas == 0) {
                throw new EntidadeNaoEncontradaException("Conta com ID " + conta.getId() + " não encontrada para atualização.");
            }
            logger.info("Conta atualizada com sucesso: ID {}", conta.getId());
        } catch (DuplicateKeyException ex) {
            String erroMsg = String.format("Erro ao atualizar conta: Agência/Número (%s/%s) já cadastrado.", conta.getAgencia(), conta.getNumeroConta());
            logger.error(erroMsg, ex);
            throw new ContaDuplicadaException(erroMsg);
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao tentar atualizar a conta: {}", conta.getId(), ex);
            throw new RepositorioException("Erro ao atualizar conta.", ex);
        }
    }

    /**
     * Deleta uma conta pelo seu ID.
     * A deleção em cascata (ON DELETE CASCADE) cuidará das entradas em tabelas filhas.
     *
     * @param id O ID da conta a ser deletada.
     * @throws EntidadeNaoEncontradaException se a conta com o ID não for encontrada.
     * @throws RepositorioException se ocorrer um erro de acesso a dados.
     */
    public void deletar(Integer id) {
        Objects.requireNonNull(id, "O ID para deleção não pode ser nulo.");
        try {
            int linhasAfetadas = jdbcTemplate.update(DELETE_BY_ID, id);
            if (linhasAfetadas == 0) {
                throw new EntidadeNaoEncontradaException("Conta com ID " + id + " não encontrada para deleção.");
            }
            logger.info("Conta deletada com sucesso: ID {}", id);
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao deletar conta pelo ID: {}", id, ex);
            throw new RepositorioException("Erro ao deletar conta.", ex);
        }
    }
}
