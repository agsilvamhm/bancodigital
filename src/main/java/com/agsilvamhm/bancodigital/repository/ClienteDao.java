package com.agsilvamhm.bancodigital.repository;


import com.agsilvamhm.bancodigital.controller.exception.CpfDuplicadoException;
import com.agsilvamhm.bancodigital.entity.CategoriaCliente;
import com.agsilvamhm.bancodigital.entity.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClienteDao  {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ClienteDao.class);


    public ClienteDao(JdbcTemplate jdbcemplate){
        this.jdbcTemplate = jdbcemplate;
    }

    public void salvar(Cliente cliente){
        String sql = "INSERT INTO cliente (cpf, nome, data_nascimento, categoria) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql,
                    cliente.getCpf(),
                    cliente.getNome(),
                    cliente.getDataNascimento(),
                    cliente.getCategoria().name());
        } catch (DuplicateKeyException ex) {
            logger.error("Erro: CPF já cadastrado - {}", cliente.getCpf(), ex);
            throw new CpfDuplicadoException("CPF já está cadastrado.");
        } catch (DataAccessException ex) {
            logger.error("Erro ao salvar cliente no banco de dados", ex);
            throw new RuntimeException("Erro ao salvar cliente.");
        }
    }

    public Cliente buscarPorId(Integer id) {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, clienteMapper());
    }

    public List<Cliente> listarTodos() {
        String sql = "SELECT * FROM cliente";
        return jdbcTemplate.query(sql, clienteMapper());
    }

    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET cpf=?, nome=?, data_nascimento=?, categoria=? WHERE id=?";
        jdbcTemplate.update(sql,
                cliente.getCpf(),
                cliente.getNome(),
                cliente.getDataNascimento(),
                cliente.getCategoria().name(),
                cliente.getId());
    }

    public void deletar(Integer id) {
        String sql = "DELETE FROM cliente WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private RowMapper<Cliente> clienteMapper() {
        return (rs, rowNum) -> {
            Cliente cliente = new Cliente();
            cliente.setId(rs.getInt("id"));
            cliente.setCpf(rs.getString("cpf"));
            cliente.setNome(rs.getString("nome"));
            cliente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
            cliente.setCategoria(CategoriaCliente.valueOf(rs.getString("categoria")));
            return cliente;
        };
    }
}
