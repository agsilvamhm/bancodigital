package com.agsilvamhm.bancodigital.Repository;

import com.agsilvamhm.bancodigital.model.*;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    // ... (Construtor e BASE_SELECT_SQL permanecem os mesmos) ...
    @Autowired
    public ContaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String BASE_SELECT_SQL = """
            SELECT
                c.id as conta_id, c.numero, c.agencia, c.saldo,
                cl.id as cliente_id, cl.cpf, cl.nome, cl.categoria as cliente_categoria,
                cc.taxa_manutencao_mensal,
                cp.taxa_rendimento_mensal,
                CASE
                    WHEN cc.id_conta IS NOT NULL THEN 'CORRENTE'
                    WHEN cp.id_conta IS NOT NULL THEN 'POUPANCA'
                END as tipo_conta
            FROM conta c
            JOIN cliente cl ON c.id_cliente = cl.id
            LEFT JOIN conta_corrente cc ON c.id = cc.id_conta
            LEFT JOIN conta_poupanca cp ON c.id = cp.id_conta
            """;

    private final RowMapper<Conta> contaRowMapper = (rs, rowNum) -> {
        // ... (lógica de criação de ContaCorrente/ContaPoupanca está correta) ...
        String tipoConta = rs.getString("tipo_conta");
        Conta conta;

        if ("CORRENTE".equals(tipoConta)) {
            ContaCorrente cc = new ContaCorrente();
            cc.setTaxaManutencaoMensal(rs.getDouble("taxa_manutencao_mensal"));
            conta = cc;
        } else if ("POUPANCA".equals(tipoConta)) {
            ContaPoupanca cp = new ContaPoupanca();
            cp.setTaxaRendimentoMensal(rs.getDouble("taxa_rendimento_mensal"));
            conta = cp;
        } else {
            conta = new Conta() {};
        }

        conta.setId(rs.getLong("conta_id"));
        // AJUSTE AQUI: Usar o setter correto.
        conta.setNumero(rs.getString("numero"));
        conta.setAgencia(rs.getString("agencia"));
        conta.setSaldo(rs.getBigDecimal("saldo"));

        // ... (mapeamento do cliente está correto) ...
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
        // ... (lógica de validação está correta) ...
        Objects.requireNonNull(conta, "O objeto conta não pode ser nulo.");
        Objects.requireNonNull(conta.getCliente(), "O cliente da conta não pode ser nulo.");
        Objects.requireNonNull(conta.getCliente().getId(), "O ID do cliente não pode ser nulo.");

        String sqlConta = "INSERT INTO conta (numero, agencia, saldo, id_cliente) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlConta, Statement.RETURN_GENERATED_KEYS);
            // AJUSTE AQUI: Usar o getter correto.
            ps.setString(1, conta.getNumero());
            ps.setString(2, conta.getAgencia());
            ps.setBigDecimal(3, conta.getSaldo());
            ps.setInt(4, conta.getCliente().getId());
            return ps;
        }, keyHolder);

        // ... (resto do método salvar e os outros métodos estão corretos) ...
        long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        conta.setId(generatedId);

        if (conta instanceof ContaCorrente) {
            String sqlCC = "INSERT INTO conta_corrente (id_conta, taxa_manutencao_mensal) VALUES (?, ?)";
            jdbcTemplate.update(sqlCC, generatedId, ((ContaCorrente) conta).getTaxaManutencaoMensal());
            logger.info("Conta Corrente salva com ID: {}", generatedId);
        } else if (conta instanceof ContaPoupanca) {
            String sqlCP = "INSERT INTO conta_poupanca (id_conta, taxa_rendimento_mensal) VALUES (?, ?)";
            jdbcTemplate.update(sqlCP, generatedId, ((ContaPoupanca) conta).getTaxaRendimentoMensal());
            logger.info("Conta Poupança salva com ID: {}", generatedId);
        } else {
            throw new IllegalArgumentException("Tipo de conta desconhecido ou não suportado para salvar.");
        }

        return conta;
    }

    // ... Os outros métodos (buscarPorId, buscarPorNumero, listar, atualizar) não precisam de alteração ...
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
        String sql = BASE_SELECT_SQL.replace("LEFT JOIN conta_corrente", "INNER JOIN conta_corrente");
        return jdbcTemplate.query(sql, contaRowMapper);
    }

    public List<Conta> listarContasPoupanca() {
        String sql = BASE_SELECT_SQL.replace("LEFT JOIN conta_poupanca", "INNER JOIN conta_poupanca");
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

        if (conta instanceof ContaCorrente) {
            String sqlCC = "UPDATE conta_corrente SET taxa_manutencao_mensal = ? WHERE id_conta = ?";
            jdbcTemplate.update(sqlCC, ((ContaCorrente) conta).getTaxaManutencaoMensal(), conta.getId());
        } else if (conta instanceof ContaPoupanca) {
            String sqlCP = "UPDATE conta_poupanca SET taxa_rendimento_mensal = ? WHERE id_conta = ?";
            jdbcTemplate.update(sqlCP, ((ContaPoupanca) conta).getTaxaRendimentoMensal(), conta.getId());
        }

        logger.info("Conta ID {} atualizada com sucesso.", conta.getId());
    }
}