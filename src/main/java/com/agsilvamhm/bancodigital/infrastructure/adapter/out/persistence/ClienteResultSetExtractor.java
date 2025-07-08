package com.agsilvamhm.bancodigital.infrastructure.adapter.out.persistence;

import com.agsilvamhm.bancodigital.core.domain.model.Cliente;
import com.agsilvamhm.bancodigital.core.domain.model.Endereco;
import com.agsilvamhm.bancodigital.model.CategoriaCliente;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClienteResultSetExtractor implements ResultSetExtractor<List<Cliente>> {

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

    public String getBaseSelectSql() {
        return BASE_SELECT_SQL;
    }

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

            if (rs.getLong("conta_id") != 0) {
                // Lógica de mapeamento de Conta (assumindo a existência das classes)
            }
        }
        return new ArrayList<>(clienteMap.values());
    }
}
