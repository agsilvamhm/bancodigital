package com.agsilvamhm.bancodigital.infrastructure.adapter.out.persistence;

import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.core.domain.model.Endereco;
import com.agsilvamhm.bancodigital.core.port.out.EnderecoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;

@Repository
public class EnderecoRepositoryAdapter implements EnderecoRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(EnderecoRepositoryAdapter.class);

    // Queries do antigo EnderecoDao
    private static final String INSERT_ENDERECO = "INSERT INTO endereco (rua, numero, complemento, cidade, estado, cep) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID = "SELECT * FROM endereco WHERE id = ?";
    private static final String UPDATE_ENDERECO = "UPDATE endereco SET rua = ?, numero = ?, complemento = ?, cidade = ?, estado = ?, cep = ? WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM endereco WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EnderecoRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Endereco salvar(Endereco endereco) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_ENDERECO, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, endereco.getRua());
                ps.setInt(2, endereco.getNumero());
                ps.setString(3, endereco.getComplemento());
                ps.setString(4, endereco.getCidade());
                ps.setString(5, endereco.getEstado());
                ps.setString(6, endereco.getCep());
                return ps;
            }, keyHolder);

            Integer id = Objects.requireNonNull(keyHolder.getKey()).intValue();
            endereco.setId(id);
            logger.info("Endereço salvo com sucesso com ID: {}", id);
            return endereco;
        } catch (DataAccessException ex) {
            throw new RepositorioException("Erro ao salvar endereço no banco de dados.", ex);
        }
    }

    @Override
    public void atualizar(Endereco endereco) {
        try {
            jdbcTemplate.update(UPDATE_ENDERECO,
                    endereco.getRua(),
                    endereco.getNumero(),
                    endereco.getComplemento(),
                    endereco.getCidade(),
                    endereco.getEstado(),
                    endereco.getCep(),
                    endereco.getId());
            logger.info("Endereço atualizado com sucesso: ID {}", endereco.getId());
        } catch (DataAccessException ex) {
            throw new RepositorioException("Erro ao atualizar endereço.", ex);
        }
    }

    @Override
    public Optional<Endereco> buscarPorId(Integer id) {
        try {
            Endereco endereco = jdbcTemplate.queryForObject(SELECT_BY_ID, new BeanPropertyRowMapper<>(Endereco.class), id);
            return Optional.ofNullable(endereco);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        } catch (DataAccessException ex) {
            throw new RepositorioException("Erro ao buscar endereço por ID.", ex);
        }
    }

    @Override
    public void deletar(Integer id) {
        try {
            jdbcTemplate.update(DELETE_BY_ID, id);
            logger.info("Endereço deletado com sucesso: ID {}", id);
        } catch (DataAccessException ex) {
            throw new RepositorioException("Erro ao deletar endereço.", ex);
        }
    }
}
