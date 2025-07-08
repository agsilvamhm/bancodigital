package com.agsilvamhm.bancodigital.infrastructure.adapter.out.persistence;

import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.core.domain.exceptions.CpfDuplicadoException;
import com.agsilvamhm.bancodigital.core.domain.exceptions.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.core.domain.model.Cliente;
import com.agsilvamhm.bancodigital.core.port.out.ClienteRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(ClienteRepositoryAdapter.class);

    // As queries SQL do antigo DAO vêm para cá
    private static final String INSERT_CLIENTE = "INSERT INTO cliente (cpf, nome, data_nascimento, categoria, id_endereco) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_CLIENTE = "UPDATE cliente SET cpf = ?, nome = ?, data_nascimento = ?, categoria = ?, id_endereco = ? WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM cliente WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final ClienteResultSetExtractor clienteResultSetExtractor;

    @Autowired
    public ClienteRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.clienteResultSetExtractor = new ClienteResultSetExtractor();
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_CLIENTE, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, cliente.getCpf());
                ps.setString(2, cliente.getNome());
                ps.setObject(3, cliente.getDataNascimento());
                ps.setString(4, cliente.getCategoria().name());
                ps.setInt(5, cliente.getEndereco().getId());
                return ps;
            }, keyHolder);

            Integer id = Objects.requireNonNull(keyHolder.getKey()).intValue();
            cliente.setId(id);
            logger.info("Cliente salvo com sucesso: {}", cliente.getCpf());
            return cliente;

        } catch (DuplicateKeyException ex) {
            logger.error("Erro ao salvar cliente: CPF já cadastrado - {}", cliente.getCpf(), ex);
            throw new CpfDuplicadoException("CPF já está cadastrado.");
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao tentar salvar o cliente: {}", cliente.getCpf(), ex);
            throw new RepositorioException("Erro ao salvar cliente no banco de dados.", ex);
        }
    }

    @Override
    public void atualizar(Cliente cliente) {
        try {
            int linhasAfetadas = jdbcTemplate.update(UPDATE_CLIENTE,
                    cliente.getCpf(),
                    cliente.getNome(),
                    cliente.getDataNascimento(),
                    cliente.getCategoria().name(),
                    cliente.getEndereco().getId(),
                    cliente.getId());
            if (linhasAfetadas == 0) {
                throw new EntidadeNaoEncontradaException("Cliente com ID " + cliente.getId() + " não encontrado para atualização.");
            }
            logger.info("Cliente atualizado com sucesso: ID {}", cliente.getId());
        } catch (DuplicateKeyException ex) {
            logger.error("Erro ao atualizar cliente: CPF já cadastrado - {}", cliente.getCpf(), ex);
            throw new CpfDuplicadoException("Não foi possível atualizar, o CPF informado já pertence a outro cliente.");
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao tentar atualizar o cliente: {}", cliente.getId(), ex);
            throw new RepositorioException("Erro ao atualizar cliente.", ex);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        try {
            List<Cliente> clientes = jdbcTemplate.query(clienteResultSetExtractor.getBaseSelectSql() + "WHERE c.id = ?", clienteResultSetExtractor, id);
            return clientes.stream().findFirst();
        } catch (DataAccessException ex) {
            throw new RepositorioException("Erro ao buscar cliente por ID.", ex);
        }
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        try {
            List<Cliente> clientes = jdbcTemplate.query(clienteResultSetExtractor.getBaseSelectSql() + "WHERE c.cpf = ?", clienteResultSetExtractor, cpf);
            return clientes.stream().findFirst();
        } catch (DataAccessException ex) {
            throw new RepositorioException("Erro ao buscar cliente por CPF.", ex);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        try {
            return jdbcTemplate.query(clienteResultSetExtractor.getBaseSelectSql() + "ORDER BY c.nome, cta.id", clienteResultSetExtractor);
        } catch (DataAccessException ex) {
            throw new RepositorioException("Erro ao listar todos os clientes.", ex);
        }
    }

    @Override
    public void deletarPorId(Integer id) {
        try {
            int linhasAfetadas = jdbcTemplate.update(DELETE_BY_ID, id);
            if (linhasAfetadas == 0) {
                throw new EntidadeNaoEncontradaException("Cliente com ID " + id + " não encontrado para deleção.");
            }
            logger.info("Cliente deletado com sucesso: ID {}", id);
        } catch (DataAccessException ex) {
            throw new RepositorioException("Erro ao deletar cliente.", ex);
        }
    }
}