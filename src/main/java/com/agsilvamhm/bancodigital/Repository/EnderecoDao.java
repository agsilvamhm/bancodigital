package com.agsilvamhm.bancodigital.Repository;

import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.model.Endereco;
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
public class EnderecoDao {

    private static final Logger logger = LoggerFactory.getLogger(EnderecoDao.class);

    private static final String INSERT_ENDERECO = "INSERT INTO endereco (rua, numero, complemento, cidade, estado, cep) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID = "SELECT * FROM endereco WHERE id = ?";
    private static final String UPDATE_ENDERECO = "UPDATE endereco SET rua = ?, numero = ?, complemento = ?, cidade = ?, estado = ?, cep = ? WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM endereco WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EnderecoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer salvar(Endereco endereco) {
        Objects.requireNonNull(endereco, "O objeto endereco não pode ser nulo.");
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

            // Extrai e retorna o ID gerado
            Integer generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
            logger.info("Endereço salvo com sucesso com ID: {}", generatedId);
            return generatedId;
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao tentar salvar o endereço.", ex);
            throw new RepositorioException("Erro ao salvar endereço no banco de dados.", ex);
        }
    }

    public Optional<Endereco> buscarPorId(Integer id) {
        try {
            Endereco endereco = jdbcTemplate.queryForObject(SELECT_BY_ID, new BeanPropertyRowMapper<>(Endereco.class), id);
            return Optional.ofNullable(endereco);
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("Nenhum endereço encontrado com o ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao buscar endereço pelo ID: {}", id, ex);
            throw new RepositorioException("Erro ao buscar endereço por ID.", ex);
        }
    }

    public void atualizar(Endereco endereco) {
        Objects.requireNonNull(endereco, "O objeto endereco não pode ser nulo.");
        Objects.requireNonNull(endereco.getId(), "O ID do endereço não pode ser nulo para atualização.");
        try {
            int linhasAfetadas = jdbcTemplate.update(UPDATE_ENDERECO,
                    endereco.getRua(),
                    endereco.getNumero(),
                    endereco.getComplemento(),
                    endereco.getCidade(),
                    endereco.getEstado(),
                    endereco.getCep(),
                    endereco.getId());

            if (linhasAfetadas == 0) {
                logger.warn("Nenhuma linha afetada ao tentar atualizar o endereço com ID: {}. Endereço pode não existir.", endereco.getId());
            }
            logger.info("Endereço atualizado com sucesso: ID {}", endereco.getId());
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao tentar atualizar o endereço: {}", endereco.getId(), ex);
            throw new RepositorioException("Erro ao atualizar endereço.", ex);
        }
    }

    public void deletar(Integer id) {
        Objects.requireNonNull(id, "O ID para deleção não pode ser nulo.");
        try {
            int linhasAfetadas = jdbcTemplate.update(DELETE_BY_ID, id);
            if (linhasAfetadas == 0) {
                logger.warn("Nenhuma linha afetada ao tentar deletar o endereço com ID: {}. Endereço pode não existir.", id);
            }
            logger.info("Endereço deletado com sucesso: ID {}", id);
        } catch (DataAccessException ex) {
            logger.error("Erro de acesso a dados ao deletar endereço pelo ID: {}", id, ex);
            throw new RepositorioException("Erro ao deletar endereço.", ex);
        }
    }
}

