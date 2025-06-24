package com.agsilvamhm.bancodigital.dao;

import com.agsilvamhm.bancodigital.controller.exception.CpfDuplicadoException;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.entity.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ClienteDao {

    private static final Logger logger = LoggerFactory.getLogger(ClienteDao.class);
    private static final String INSERT_CLIENTE = "INSERT INTO cliente (cpf, nome, data_nascimento, categoria) VALUES (?, ?, ?, ?)";
    private static final String SELECT_BY_ID = "SELECT * FROM cliente WHERE id = ?";
    private static final String SELECT_ALL = "SELECT * FROM cliente";
    private static final String UPDATE_CLIENTE = "UPDATE cliente SET cpf = ?, nome = ?, data_nascimento = ?, categoria = ? WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM cliente WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    public ClienteDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void salvar(Cliente cliente) {
        Objects.requireNonNull(cliente, "O objeto cliente não pode ser nulo.");
        try {
            jdbcTemplate.update(INSERT_CLIENTE,
                    cliente.getCpf(),
                    cliente.getNome(),
                    cliente.getDataNascimento(),
                    cliente.getCategoria().name());
            logger.info("Cliente salvo com sucesso: {}", cliente.getCpf());
        } catch (DuplicateKeyException ex) {
            logger.error("Erro ao salvar cliente: CPF já cadastrado - {}", cliente.getCpf(), ex);
            throw new CpfDuplicadoException("CPF já está cadastrado.");
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao tentar salvar o cliente: {}", cliente.getCpf(), ex);
            throw new RepositorioException("Erro ao salvar cliente no banco de dados.", ex);
        }
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        // --- Boas Práticas: Retornar Optional para evitar NullPointerException ---
        try {
            Cliente cliente = jdbcTemplate.queryForObject(SELECT_BY_ID, new BeanPropertyRowMapper<>(Cliente.class), id);
            return Optional.ofNullable(cliente);
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("Nenhum cliente encontrado com o ID: {}", id);
            return Optional.empty(); // Retorna um Optional vazio, uma prática moderna e segura.
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao buscar cliente pelo ID: {}", id, ex);
            throw new RepositorioException("Erro ao buscar cliente por ID.", ex);
        }
    }

    public List<Cliente> listarTodos() {
        try {
            return jdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(Cliente.class));
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao listar todos os clientes.", ex);
            throw new RepositorioException("Erro ao listar todos os clientes.", ex);
        }
    }

    public void atualizar(Cliente cliente) {
        Objects.requireNonNull(cliente, "O objeto cliente não pode ser nulo.");
        Objects.requireNonNull(cliente.getId(), "O ID do cliente não pode ser nulo para atualização.");
        try {
            int linhasAfetadas = jdbcTemplate.update(UPDATE_CLIENTE,
                    cliente.getCpf(),
                    cliente.getNome(),
                    cliente.getDataNascimento(),
                    cliente.getCategoria().name(),
                    cliente.getId());

            if (linhasAfetadas == 0) {
                throw new EntidadeNaoEncontradaException("Cliente com ID " + cliente.getId() + " não encontrado para atualização.");
            }
            logger.info("Cliente atualizado com sucesso: ID {}", cliente.getId());
        } catch (DuplicateKeyException ex) {
            logger.error("Erro ao atualizar cliente: CPF já cadastrado - {}", cliente.getCpf(), ex);
            throw new CpfDuplicadoException("Não foi possível atualizar, o CPF informado já pertence a outro cliente. <br>" + ex.getMessage());
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao tentar atualizar o cliente: {}", cliente.getId(), ex);
            throw new RepositorioException("Erro ao atualizar cliente.", ex);
        }
    }

    public void deletar(Integer id) {
        Objects.requireNonNull(id, "O ID para deleção não pode ser nulo.");
        try {
            int linhasAfetadas = jdbcTemplate.update(DELETE_BY_ID, id);
            if (linhasAfetadas == 0) {
                throw new EntidadeNaoEncontradaException("Cliente com ID " + id + " não encontrado para deleção.");
            }
            logger.info("Cliente deletado com sucesso: ID {}", id);
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao deletar cliente pelo ID: {}", id, ex);
            throw new RepositorioException("Erro ao deletar cliente.", ex);
        }
    }
}