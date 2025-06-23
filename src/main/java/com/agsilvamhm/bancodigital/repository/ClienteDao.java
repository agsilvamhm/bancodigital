package com.agsilvamhm.bancodigital.repository;


import com.agsilvamhm.bancodigital.controller.exception.CpfDuplicadoException;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.entity.CategoriaCliente;
import com.agsilvamhm.bancodigital.entity.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ClienteDao {

    private static final Logger logger = LoggerFactory.getLogger(ClienteDao.class);

    // --- Boas Práticas: Constantes para Queries SQL ---
    // Melhora a legibilidade e facilita a manutenção, evitando "magic strings".
    private static final String INSERT_CLIENTE = "INSERT INTO cliente (cpf, nome, data_nascimento, categoria) VALUES (?, ?, ?, ?)";
    private static final String SELECT_BY_ID = "SELECT * FROM cliente WHERE id = ?";
    private static final String SELECT_ALL = "SELECT * FROM cliente";
    private static final String UPDATE_CLIENTE = "UPDATE cliente SET cpf = ?, nome = ?, data_nascimento = ?, categoria = ? WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM cliente WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    // --- Boa Prática: Injeção de Dependências via Construtor ---
    // Corrigido o nome do parâmetro de "jdbcemplate" para "jdbcTemplate".
    public ClienteDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Salva um novo cliente no banco de dados.
     * @param cliente O objeto Cliente a ser salvo. Não pode ser nulo.
     * @throws CpfDuplicadoException se o CPF já existir.
     * @throws RepositorioException para outros erros de acesso a dados.
     */
    public void salvar(Cliente cliente) {
        // --- Boas Práticas: Validação de Parâmetros (Fail-Fast) ---
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
            throw new CpfDuplicadoException("CPF já está cadastrado. <br>" + ex.getMessage());
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao tentar salvar o cliente: {}", cliente.getCpf(), ex);
            throw new RepositorioException("Erro ao salvar cliente no banco de dados.", ex);
        }
    }

    /**
     * Busca um cliente pelo seu ID.
     * @param id O ID do cliente.
     * @return um Optional contendo o Cliente se encontrado, ou Optional.empty() caso contrário.
     * @throws RepositorioException para erros de acesso a dados.
     */
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

    /**
     * Lista todos os clientes cadastrados.
     * @return uma Lista de Clientes. A lista estará vazia se não houver clientes.
     * @throws RepositorioException para erros de acesso a dados.
     */
    public List<Cliente> listarTodos() {
        try {
            // --- Boas Práticas: Usar BeanPropertyRowMapper para simplificar o mapeamento ---
            // Isso funciona se os nomes das colunas do DB correspondem aos nomes das propriedades da classe Cliente.
            return jdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(Cliente.class));
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao listar todos os clientes.", ex);
            throw new RepositorioException("Erro ao listar todos os clientes.", ex);
        }
    }

    /**
     * Atualiza os dados de um cliente existente.
     * @param cliente O objeto Cliente com os dados atualizados. Não pode ser nulo.
     * @throws EntidadeNaoEncontradaException se o cliente com o ID especificado não existir.
     * @throws CpfDuplicadoException se a alteração do CPF resultar em duplicidade.
     * @throws RepositorioException para outros erros de acesso a dados.
     */
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

            // --- Boas Práticas: Verificar se a atualização teve efeito ---
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

    /**
     * Deleta um cliente pelo seu ID.
     * @param id O ID do cliente a ser deletado.
     * @throws EntidadeNaoEncontradaException se o cliente com o ID especificado não existir.
     * @throws RepositorioException para erros de acesso a dados.
     */
    public void deletar(Integer id) {
        Objects.requireNonNull(id, "O ID para deleção não pode ser nulo.");
        try {
            int linhasAfetadas = jdbcTemplate.update(DELETE_BY_ID, id);

            // --- Boas Práticas: Verificar se a deleção teve efeito ---
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