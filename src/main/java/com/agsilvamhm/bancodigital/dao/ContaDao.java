package com.agsilvamhm.bancodigital.dao;

import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.entity.*;
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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ContaDao {

    private static final Logger logger = LoggerFactory.getLogger(ContaDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final ContaRowMapper contaRowMapper = new ContaRowMapper();

    private static final String BASE_SELECT_SQL =
            "SELECT " +
                    "cta.id as conta_id, cta.numero, cta.agencia, cta.saldo, cta.tipo_conta, " +
                    "cli.id as cliente_id, cli.cpf, cli.nome, cli.data_nascimento, cli.categoria, " +
                    "end.id as endereco_id, end.rua, end.numero as endereco_numero, end.complemento, end.cidade, end.estado, end.cep " +
                    "FROM conta cta " +
                    "JOIN cliente cli ON cta.id_cliente = cli.id " +
                    "JOIN endereco end ON cli.id_endereco = end.id ";

    private static final String INSERT_CONTA = "INSERT INTO conta (numero, agencia, saldo, id_cliente, tipo_conta) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_CONTA_SALDO = "UPDATE conta SET saldo = ? WHERE id = ?";
    private static final String SELECT_BY_ID = BASE_SELECT_SQL + "WHERE cta.id = ?";
    private static final String SELECT_BY_CLIENTE_ID = BASE_SELECT_SQL + "WHERE cta.id_cliente = ?";
    private static final String SELECT_ALL_CORRENTE = BASE_SELECT_SQL + "WHERE cta.tipo_conta = 'CORRENTE'";
    private static final String SELECT_ALL_POUPANCA = BASE_SELECT_SQL + "WHERE cta.tipo_conta = 'POUPANCA'";

    @Autowired
    public ContaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer salvar(Conta conta) {
        Objects.requireNonNull(conta, "O objeto conta não pode ser nulo.");
        Objects.requireNonNull(conta.getCliente(), "O cliente da conta não pode ser nulo.");
        Objects.requireNonNull(conta.getCliente().getId(), "O ID do cliente da conta não pode ser nulo.");

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_CONTA, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, conta.getNumero());
                ps.setString(2, conta.getAgencia());
                ps.setDouble(3, conta.getSaldo());
                ps.setInt(4, conta.getCliente().getId());

                if (conta instanceof ContaCorrente) {
                    ps.setString(5, "CORRENTE");
                } else if (conta instanceof ContaPoupanca) {
                    ps.setString(5, "POUPANCA");
                } else {
                    throw new IllegalArgumentException("Tipo de conta não suportado: " + conta.getClass().getName());
                }
                return ps;
            }, keyHolder);

            Integer generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
            logger.info("Conta ID {} salva com sucesso para o cliente ID {}.", generatedId, conta.getCliente().getId());
            return generatedId;
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao salvar conta.", ex);
            throw new RepositorioException("Erro ao salvar conta no banco de dados.", ex);
        }
    }

    public Optional<Conta> buscarPorId(Integer id) {
        try {
            Conta conta = jdbcTemplate.queryForObject(SELECT_BY_ID, contaRowMapper, id);
            return Optional.ofNullable(conta);
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("Nenhuma conta encontrada com o ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao buscar conta pelo ID: {}", id, ex);
            throw new RepositorioException("Erro ao buscar conta por ID.", ex);
        }
    }

    public List<Conta> buscarPorCliente(Integer idCliente) {
        try {
            return jdbcTemplate.query(SELECT_BY_CLIENTE_ID, contaRowMapper, idCliente);
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao buscar contas para o cliente ID: {}", idCliente, ex);
            throw new RepositorioException("Erro ao buscar contas do cliente.", ex);
        }
    }

    public void atualizarSaldo(Integer idConta, double novoSaldo) {
        try {
            int rowsAffected = jdbcTemplate.update(UPDATE_CONTA_SALDO, novoSaldo, idConta);
            if(rowsAffected == 0){
                logger.warn("Nenhuma linha afetada ao tentar atualizar saldo da conta ID: {}", idConta);
            }
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao atualizar saldo da conta ID: {}", idConta, ex);
            throw new RepositorioException("Erro ao atualizar saldo da conta.", ex);
        }
    }

    public List<ContaCorrente> buscarTodasContasCorrentes() {
        return jdbcTemplate.query(SELECT_ALL_CORRENTE, rs -> {
            List<ContaCorrente> contas = new ArrayList<>();
            while (rs.next()) {
                contas.add((ContaCorrente) contaRowMapper.mapRow(rs, rs.getRow()));
            }
            return contas;
        });
    }

    public List<ContaPoupanca> buscarTodasContasPoupanca() {
        return jdbcTemplate.query(SELECT_ALL_POUPANCA, rs -> {
            List<ContaPoupanca> contas = new ArrayList<>();
            while (rs.next()) {
                contas.add((ContaPoupanca) contaRowMapper.mapRow(rs, rs.getRow()));
            }
            return contas;
        });
    }

    private static class ContaRowMapper implements RowMapper<Conta> {
        @Override
        public Conta mapRow(ResultSet rs, int rowNum) throws SQLException {
            String tipoConta = rs.getString("tipo_conta");
            Conta conta;

            if ("CORRENTE".equals(tipoConta)) {
                conta = new ContaCorrente();
            } else if ("POUPANCA".equals(tipoConta)) {
                conta = new ContaPoupanca();
            } else {
                throw new SQLException("Tipo de conta desconhecido no banco de dados: " + tipoConta);
            }

            conta.setId(rs.getInt("conta_id"));
            conta.setNumero(rs.getString("numero"));
            conta.setAgencia(rs.getString("agencia"));
            conta.setSaldo(rs.getDouble("saldo"));

            Endereco endereco = new Endereco();
            endereco.setId(rs.getInt("endereco_id"));
            endereco.setRua(rs.getString("rua"));
            endereco.setNumero(rs.getInt("endereco_numero"));
            endereco.setComplemento(rs.getString("complemento"));
            endereco.setCidade(rs.getString("cidade"));
            endereco.setEstado(rs.getString("estado"));
            endereco.setCep(rs.getString("cep"));

            Cliente cliente = new Cliente();
            cliente.setId(rs.getInt("cliente_id"));
            cliente.setCpf(rs.getString("cpf"));
            cliente.setNome(rs.getString("nome"));
            Date dataNascimento = rs.getDate("data_nascimento");
            if (dataNascimento != null) {
                cliente.setDataNascimento(dataNascimento.toLocalDate());
            }
            String categoria = rs.getString("categoria");
            if (categoria != null) {
                cliente.setCategoria(CategoriaCliente.valueOf(categoria.toUpperCase()));
            }
            cliente.setEndereco(endereco);

            conta.setCliente(cliente);
            return conta;
        }
    }
}