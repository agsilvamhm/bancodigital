package com.agsilvamhm.bancodigital.Repository;

import com.agsilvamhm.bancodigital.model.CategoriaCliente;
import com.agsilvamhm.bancodigital.model.Cliente;
import com.agsilvamhm.bancodigital.model.Conta;
import com.agsilvamhm.bancodigital.model.Endereco;
import com.agsilvamhm.bancodigital.controller.exception.CpfDuplicadoException;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.model.ContaCorrente;
import com.agsilvamhm.bancodigital.model.ContaPoupanca;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ClienteDao {

    private static final Logger logger = LoggerFactory.getLogger(ClienteDao.class);
    private static final String BASE_SELECT_SQL =
            "SELECT " +
                    "c.id as cliente_id, c.cpf, c.nome, c.data_nascimento, c.categoria, " +
                    "e.id as endereco_id, e.rua, e.numero as endereco_numero, e.complemento, e.cidade, e.estado, e.cep, " +
                    "cta.id as conta_id, cta.numero as conta_numero, cta.agencia, cta.saldo, " +
                    "cc.taxa_manutencao_mensal, " +
                    "cp.taxa_rendimento_mensal, " +
                    "CASE " +
                    "    WHEN cc.id_conta IS NOT NULL THEN 'CORRENTE' " +
                    "    WHEN cp.id_conta IS NOT NULL THEN 'POUPANCA' " +
                    "END as tipo_conta " +
                    "FROM cliente c " +
                    "LEFT JOIN endereco e ON c.id_endereco = e.id " +
                    "LEFT JOIN conta cta ON c.id = cta.id_cliente " +
                    "LEFT JOIN conta_corrente cc ON cta.id = cc.id_conta " +
                    "LEFT JOIN conta_poupanca cp ON cta.id = cp.id_conta ";

    private static final String INSERT_CLIENTE = "INSERT INTO cliente (cpf, nome, data_nascimento, categoria, id_endereco) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID = BASE_SELECT_SQL + "WHERE c.id = ?";
    private static final String SELECT_ALL = BASE_SELECT_SQL + "ORDER BY c.nome, cta.id";
    private static final String UPDATE_CLIENTE = "UPDATE cliente SET cpf = ?, nome = ?, data_nascimento = ?, categoria = ? WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM cliente WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClienteDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void salvar(Cliente cliente) {
        Objects.requireNonNull(cliente, "O objeto cliente não pode ser nulo.");
        Objects.requireNonNull(cliente.getEndereco(), "O endereço do cliente não pode ser nulo.");
        Objects.requireNonNull(cliente.getEndereco().getId(), "O ID do endereço do cliente não pode ser nulo.");

        try {
            jdbcTemplate.update(INSERT_CLIENTE,
                    cliente.getCpf(),
                    cliente.getNome(),
                    cliente.getDataNascimento(),
                    cliente.getCategoria().name(),
                    cliente.getEndereco().getId());
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
        try {
            List<Cliente> clientes = jdbcTemplate.query(SELECT_BY_ID, new ClienteResultSetExtractor(), id);
            return clientes.stream().findFirst();
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao buscar cliente pelo ID: {}", id, ex);
            throw new RepositorioException("Erro ao buscar cliente por ID.", ex);
        }
    }

    public List<Cliente> listarTodos() {
        try {
            return jdbcTemplate.query(SELECT_ALL, new ClienteResultSetExtractor());
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

    private static class ClienteResultSetExtractor implements ResultSetExtractor<List<Cliente>> {
        @Override
        public List<Cliente> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Integer, Cliente> clienteMap = new LinkedHashMap<>();

            while (rs.next()) {
                Integer clienteId = rs.getInt("cliente_id");
                Cliente cliente = clienteMap.computeIfAbsent(clienteId, id -> {
                    try {
                        Cliente novoCliente = new Cliente();
                        novoCliente.setId(id);
                        novoCliente.setCpf(rs.getString("cpf"));
                        novoCliente.setNome(rs.getString("nome"));
                        if (rs.getDate("data_nascimento") != null) {
                            novoCliente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                        }
                        if (rs.getString("categoria") != null) {
                            novoCliente.setCategoria(CategoriaCliente.valueOf(rs.getString("categoria")));
                        }

                        novoCliente.setContas(new ArrayList<>());

                        if (rs.getInt("endereco_id") != 0) {
                            Endereco endereco = new Endereco();
                            endereco.setId(rs.getInt("endereco_id"));
                            endereco.setRua(rs.getString("rua"));
                            endereco.setNumero(rs.getInt("endereco_numero"));
                            endereco.setComplemento(rs.getString("complemento"));
                            endereco.setCidade(rs.getString("cidade"));
                            endereco.setEstado(rs.getString("estado"));
                            endereco.setCep(rs.getString("cep"));
                            novoCliente.setEndereco(endereco);
                        }
                        return novoCliente;
                    } catch (SQLException e) {
                        throw new RuntimeException("Erro ao mapear cliente.", e);
                    }
                });

                if (rs.getInt("conta_id") != 0) {
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
                        continue;
                    }

                    conta.setId(rs.getLong("conta_id"));
                    conta.setNumero(rs.getString("conta_numero"));
                    conta.setAgencia(rs.getString("agencia"));
                    conta.setSaldo(rs.getBigDecimal("saldo"));
                    conta.setCliente(cliente); // Associa a conta ao cliente

                    if (cliente.getContas().stream().noneMatch(c -> c.getId().equals(conta.getId()))) {
                        cliente.getContas().add(conta);
                    }
                }
            }
            return new ArrayList<>(clienteMap.values());
        }
    }
}