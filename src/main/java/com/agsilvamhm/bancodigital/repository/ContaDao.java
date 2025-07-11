package com.agsilvamhm.bancodigital.repository;

import com.agsilvamhm.bancodigital.model.*;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ContaDao {

    private static final Logger logger = LoggerFactory.getLogger(ContaDao.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ContaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String BASE_SELECT_SQL = """
            SELECT
                c.id as conta_id, c.numero, c.agencia, c.saldo, c.tipo_conta,
                cl.id as cliente_id, cl.cpf, cl.nome, cl.categoria as cliente_categoria
            FROM conta c
            JOIN cliente cl ON c.id_cliente = cl.id
            """;

    private final RowMapper<Conta> contaRowMapper = (rs, rowNum) -> {
        String tipoConta = rs.getString("tipo_conta");
        Conta conta;

        if ("CORRENTE".equals(tipoConta)) {
            conta = new ContaCorrente();
        } else if ("POUPANCA".equals(tipoConta)) {
            conta = new ContaPoupanca();
        } else {
            throw new IllegalStateException("Tipo de conta desconhecido no banco de dados: " + tipoConta);
        }

        conta.setId(rs.getLong("conta_id"));
        conta.setNumero(rs.getString("numero"));
        conta.setAgencia(rs.getString("agencia"));
        conta.setSaldo(rs.getBigDecimal("saldo"));

        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("cliente_id"));
        cliente.setCpf(rs.getString("cpf"));
        cliente.setNome(rs.getString("nome"));
        cliente.setCategoria(CategoriaCliente.valueOf(rs.getString("cliente_categoria")));
        conta.setCliente(cliente);

        return conta;
    };

    @Transactional
    public Conta salvar(Conta conta) {
        Objects.requireNonNull(conta, "O objeto conta não pode ser nulo.");
        Objects.requireNonNull(conta.getCliente(), "O cliente da conta não pode ser nulo.");
        Objects.requireNonNull(conta.getCliente().getId(), "O ID do cliente não pode ser nulo.");

        final String tipoConta;
        if (conta instanceof ContaCorrente) {
            tipoConta = "CORRENTE";
        } else if (conta instanceof ContaPoupanca) {
            tipoConta = "POUPANCA";
        } else {
            throw new IllegalArgumentException("Tipo de conta desconhecido ou não suportado para salvar.");
        }

        String sql = "INSERT INTO conta (tipo_conta, numero, agencia, saldo, id_cliente) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tipoConta);
            ps.setString(2, conta.getNumero());
            ps.setString(3, conta.getAgencia());
            ps.setBigDecimal(4, conta.getSaldo());
            ps.setInt(5, conta.getCliente().getId());
            return ps;
        }, keyHolder);

        long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        conta.setId(generatedId);

        logger.info("Conta do tipo {} salva com ID: {}", tipoConta, generatedId);
        return conta;
    }

    public Optional<Conta> buscarPorId(Long id) {
        String sql = BASE_SELECT_SQL + " WHERE c.id = ?";
        try {
            Conta conta = jdbcTemplate.queryForObject(sql, contaRowMapper, id);
            return Optional.ofNullable(conta);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Nenhuma conta encontrada com ID: {}", id);
            return Optional.empty();
        }
    }

    public Optional<Conta> buscarPorNumero(String numero) {
        String sql = BASE_SELECT_SQL + " WHERE c.numero = ?";
        try {
            Conta conta = jdbcTemplate.queryForObject(sql, contaRowMapper, numero);
            return Optional.ofNullable(conta);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Nenhuma conta encontrada com o número: {}", numero);
            return Optional.empty();
        }
    }

    public List<Conta> listarContasCorrente() {
        String sql = BASE_SELECT_SQL + " WHERE c.tipo_conta = 'CORRENTE'";
        return jdbcTemplate.query(sql, contaRowMapper);
    }

     public List<Conta> listarContasPoupanca() {
        String sql = BASE_SELECT_SQL + " WHERE c.tipo_conta = 'POUPANCA'";
        return jdbcTemplate.query(sql, contaRowMapper);
    }

    @Transactional
    public void atualizar(Conta conta) throws RepositorioException {
        Objects.requireNonNull(conta, "O objeto conta não pode ser nulo.");
        Objects.requireNonNull(conta.getId(), "O ID da conta não pode ser nulo para atualização.");

        String sqlConta = "UPDATE conta SET saldo = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sqlConta, conta.getSaldo(), conta.getId());

        if (rowsAffected == 0) {
            throw new RepositorioException("Conta com ID " + conta.getId() + " não encontrada para atualização.");
        }

        logger.info("Conta ID {} atualizada com sucesso.", conta.getId());
    }

    public List<Conta> buscarPorClienteId(Integer clienteId) {
        String sql = BASE_SELECT_SQL + " WHERE c.id_cliente = ?";
        try {
            return jdbcTemplate.query(sql, contaRowMapper, clienteId);
        } catch (DataAccessException ex) {
            logger.error("Erro ao buscar contas para o cliente ID: {}", clienteId, ex);
            throw new RepositorioException("Erro ao buscar contas do cliente.", ex);
        }
    }
}