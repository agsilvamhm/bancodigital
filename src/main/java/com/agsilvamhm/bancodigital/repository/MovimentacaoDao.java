package com.agsilvamhm.bancodigital.repository;

import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.model.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class MovimentacaoDao {

    private static final Logger logger = LoggerFactory.getLogger(MovimentacaoDao.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MovimentacaoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ATENÇÃO: Adicione 'id_cartao' à sua tabela 'movimentacao' no banco de dados!
    private static final String INSERT_MOVIMENTACAO = """
        INSERT INTO movimentacao (tipo, valor, data_hora, id_conta_origem, id_conta_destino, id_cartao, descricao)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

    @Transactional // Adicione @Transactional para o método salvar
    public void salvar(Movimentacao movimentacao) {
        Objects.requireNonNull(movimentacao, "Objeto de movimentação não pode ser nulo.");

        Long idOrigem = (movimentacao.getContaOrigem() != null) ? movimentacao.getContaOrigem().getId() : null;
        Long idDestino = (movimentacao.getContaDestino() != null) ? movimentacao.getContaDestino().getId() : null;
        Integer idCartao = (movimentacao.getCartao() != null) ? movimentacao.getCartao().getId() : null; // NOVO: id_cartao

        Object[] params = {
                movimentacao.getTipo().name(),
                movimentacao.getValor(),
                Timestamp.valueOf(movimentacao.getDataHora()),
                idOrigem,
                idDestino,
                idCartao, // NOVO: Parâmetro para id_cartao
                movimentacao.getDescricao()
        };

        int[] types = {
                Types.VARCHAR,
                Types.DOUBLE, // Use DOUBLE para o tipo double da sua Movimentacao
                Types.TIMESTAMP,
                Types.BIGINT,      // id_conta_origem
                Types.BIGINT,      // id_conta_destino
                Types.INTEGER,     // NOVO: id_cartao
                Types.VARCHAR
        };

        jdbcTemplate.update(INSERT_MOVIMENTACAO, params, types);

        logger.info("Movimentação do tipo {} no valor de {} salva com sucesso.",
                movimentacao.getTipo().getDescricao(), movimentacao.getValor()); // Usando getDescricao() do enum
    }

    // Base SELECT com todos os JOINS necessários para mapear Contas, Clientes e Cartões
    // ATENÇÃO: As colunas aqui devem estar no seu banco de dados e nos JOINS
    // Certifique-se de que sua tabela 'conta' tem um 'id_cliente' e 'tipo_conta'
    // e que sua tabela 'cliente' tem 'cpf', 'data_nascimento', 'categoria'
    // e sua tabela 'cartao' tem 'numero', 'nome_titular', 'tipo_cartao', 'limite_credito'
    private static final String BASE_SELECT_MOVIMENTACAO = """
            SELECT
                m.id as mov_id, m.tipo, m.valor, m.data_hora, m.descricao,
                m.id_conta_origem, orig.numero as num_conta_origem, orig.agencia as ag_conta_origem, orig.saldo as saldo_conta_origem, orig.tipo_conta as tipo_conta_origem,
                orig_cl.id as id_cliente_origem, orig_cl.cpf as cpf_cliente_origem, orig_cl.nome as nome_cliente_origem, orig_cl.data_nascimento as dt_nasc_cliente_origem, orig_cl.categoria as cat_cliente_origem,
                m.id_conta_destino, dest.numero as num_conta_destino, dest.agencia as ag_conta_destino, dest.saldo as saldo_conta_destino, dest.tipo_conta as tipo_conta_destino,
                dest_cl.id as id_cliente_destino, dest_cl.cpf as cpf_cliente_destino, dest_cl.nome as nome_cliente_destino, dest_cl.data_nascimento as dt_nasc_cliente_destino, dest_cl.categoria as cat_cliente_destino,
                m.id_cartao, ca.numero as num_cartao, ca.nome_titular as nome_titular_cartao, ca.tipo_cartao as tipo_cartao_cartao, ca.limite_credito as limite_credito_cartao
            FROM
                movimentacao m
            LEFT JOIN conta orig ON m.id_conta_origem = orig.id
            LEFT JOIN cliente orig_cl ON orig.id_cliente = orig_cl.id
            LEFT JOIN conta dest ON m.id_conta_destino = dest.id
            LEFT JOIN cliente dest_cl ON dest.id_cliente = dest_cl.id
            LEFT JOIN cartao ca ON m.id_cartao = ca.id
            """;


    private final RowMapper<Movimentacao> movimentacaoRowMapper = (rs, rowNum) -> {
        Movimentacao mov = new Movimentacao();
        mov.setId(rs.getInt("mov_id"));
        mov.setTipo(TipoMovimentacao.valueOf(rs.getString("tipo")));
        mov.setValor(rs.getDouble("valor"));
        mov.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        mov.setDescricao(rs.getString("descricao"));

        // Mapeamento da Conta de Origem
        if (rs.getObject("id_conta_origem") != null) {
            Cliente clienteOrigem = new Cliente();
            clienteOrigem.setId(rs.getInt("id_cliente_origem"));
            clienteOrigem.setCpf(rs.getString("cpf_cliente_origem"));
            clienteOrigem.setNome(rs.getString("nome_cliente_origem"));
            if (rs.getTimestamp("dt_nasc_cliente_origem") != null) { // Usar getTimestamp para LocalDateTime/LocalDate
                clienteOrigem.setDataNascimento(rs.getTimestamp("dt_nasc_cliente_origem").toLocalDateTime().toLocalDate());
            }
            if (rs.getString("cat_cliente_origem") != null) {
                clienteOrigem.setCategoria(CategoriaCliente.valueOf(rs.getString("cat_cliente_origem")));
            }

            String tipoContaOrigemStr = rs.getString("tipo_conta_origem");
            Conta contaOrigem;
            if (tipoContaOrigemStr != null) {
                if (TipoConta.CORRENTE.name().equals(tipoContaOrigemStr)) {
                    contaOrigem = new ContaCorrente();
                } else if (TipoConta.POUPANCA.name().equals(tipoContaOrigemStr)) {
                    contaOrigem = new ContaPoupanca();
                } else {
                    logger.warn("Tipo de conta de origem desconhecido no mapeamento: {}", tipoContaOrigemStr);
                    contaOrigem = new ContaCorrente(); // Fallback para um tipo padrão, ou lance uma exceção
                }
            } else {
                logger.warn("Tipo de conta de origem nulo para a conta ID: {}", rs.getLong("id_conta_origem"));
                contaOrigem = new ContaCorrente(); // Fallback
            }

            contaOrigem.setId(rs.getLong("id_conta_origem"));
            contaOrigem.setNumero(rs.getString("num_conta_origem"));
            contaOrigem.setAgencia(rs.getString("ag_conta_origem"));
            contaOrigem.setSaldo(rs.getBigDecimal("saldo_conta_origem"));
            contaOrigem.setCliente(clienteOrigem);
            mov.setContaOrigem(contaOrigem);
        }

        // Mapeamento da Conta de Destino
        if (rs.getObject("id_conta_destino") != null) {
            Cliente clienteDestino = new Cliente();
            clienteDestino.setId(rs.getInt("id_cliente_destino"));
            clienteDestino.setCpf(rs.getString("cpf_cliente_destino"));
            clienteDestino.setNome(rs.getString("nome_cliente_destino"));
            if (rs.getTimestamp("dt_nasc_cliente_destino") != null) {
                clienteDestino.setDataNascimento(rs.getTimestamp("dt_nasc_cliente_destino").toLocalDateTime().toLocalDate());
            }
            if (rs.getString("cat_cliente_destino") != null) {
                clienteDestino.setCategoria(CategoriaCliente.valueOf(rs.getString("cat_cliente_destino")));
            }

            String tipoContaDestinoStr = rs.getString("tipo_conta_destino");
            Conta contaDestino;
            if (tipoContaDestinoStr != null) {
                if (TipoConta.CORRENTE.name().equals(tipoContaDestinoStr)) {
                    contaDestino = new ContaCorrente();
                } else if (TipoConta.POUPANCA.name().equals(tipoContaDestinoStr)) {
                    contaDestino = new ContaPoupanca();
                } else {
                    logger.warn("Tipo de conta de destino desconhecido no mapeamento: {}", tipoContaDestinoStr);
                    contaDestino = new ContaCorrente(); // Fallback
                }
            } else {
                logger.warn("Tipo de conta de destino nulo para a conta ID: {}", rs.getLong("id_conta_destino"));
                contaDestino = new ContaCorrente(); // Fallback
            }
            contaDestino.setId(rs.getLong("id_conta_destino"));
            contaDestino.setNumero(rs.getString("num_conta_destino"));
            contaDestino.setAgencia(rs.getString("ag_conta_destino"));
            contaDestino.setSaldo(rs.getBigDecimal("saldo_conta_destino"));
            contaDestino.setCliente(clienteDestino);
            mov.setContaDestino(contaDestino);
        }

        // Mapeamento do Cartão (NOVO)
        if (rs.getObject("id_cartao") != null) {
            Cartao cartao = new Cartao();
            cartao.setId(rs.getInt("id_cartao"));
            cartao.setNumero(rs.getString("num_cartao"));
            cartao.setNomeTitular(rs.getString("nome_titular_cartao"));
            if (rs.getString("tipo_cartao_cartao") != null) {
                cartao.setTipoCartao(TipoCartao.valueOf(rs.getString("tipo_cartao_cartao")));
            }
            cartao.setLimiteCredito(rs.getBigDecimal("limite_credito_cartao"));
            mov.setCartao(cartao);
        }

        return mov;
    };

    public Optional<Movimentacao> buscarPorId(Integer id) {
        String sql = BASE_SELECT_MOVIMENTACAO + " WHERE m.id = ?";
        try {
            Movimentacao movimentacao = jdbcTemplate.queryForObject(sql, movimentacaoRowMapper, id);
            return Optional.ofNullable(movimentacao);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            logger.warn("Nenhuma movimentação encontrada com ID: {}", id);
            return Optional.empty();
        }
    }

    public List<Movimentacao> buscarPorContaId(Long contaId) {
        final String sql = BASE_SELECT_MOVIMENTACAO + """
            WHERE
                m.id_conta_origem = ? OR m.id_conta_destino = ?
            ORDER BY
                m.data_hora DESC
        """;

        try {
            return jdbcTemplate.query(sql, movimentacaoRowMapper, contaId, contaId);
        } catch (DataAccessException e) {
            logger.error("Erro ao buscar movimentações para a conta ID: {}", contaId, e);
            throw new RepositorioException("Erro ao acessar o extrato da conta.", e);
        }
    }

    // NOVO MÉTODO: Busca gastos de crédito por cartão e período
    // Usado no CartaoService para gerar a fatura
    public List<Movimentacao> buscarGastosCreditoPorCartaoEMes(Integer cartaoId, YearMonth mesReferencia) {
        // Define o início e fim do mês
        LocalDateTime inicioMes = mesReferencia.atDay(1).atStartOfDay();
        LocalDateTime fimMes = mesReferencia.atEndOfMonth().atTime(23, 59, 59);

        final String sql = BASE_SELECT_MOVIMENTACAO + """
                WHERE m.id_cartao = ?
                AND m.tipo = ?
                AND m.data_hora >= ?
                AND m.data_hora <= ?
                ORDER BY m.data_hora ASC
                """;
        try {
            return jdbcTemplate.query(sql, movimentacaoRowMapper,
                    cartaoId, TipoMovimentacao.COMPRA_CREDITO.name(), inicioMes, fimMes);
        } catch (DataAccessException e) {
            logger.error("Erro ao buscar gastos de crédito para o cartão ID {} no mês {}: {}", cartaoId, mesReferencia, e.getMessage());
            throw new RepositorioException("Erro ao consultar gastos do cartão de crédito.", e);
        }
    }
}