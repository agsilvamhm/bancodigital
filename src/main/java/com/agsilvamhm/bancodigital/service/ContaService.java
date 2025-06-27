package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.dao.ClienteDao;
import com.agsilvamhm.bancodigital.dao.ContaDao;
import com.agsilvamhm.bancodigital.entity.Conta;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ContaService {

    private static final Logger logger = LoggerFactory.getLogger(ContaService.class);

    private final ContaDao contaDao;
    private final ClienteDao clienteDao; // Dependência para validar a existência do cliente

    @Autowired
    public ContaService(ContaDao contaDao, ClienteDao clienteDao) {
        this.contaDao = contaDao;
        this.clienteDao = clienteDao;
    }

    /**
     * Cria uma nova conta no sistema e retorna o objeto com seu ID.
     * @param conta A conta a ser criada.
     * @return A conta após ser salva com o ID atribuído.
     */
    @Transactional
    public Conta criarConta(Conta conta) {
        Objects.requireNonNull(conta, "O objeto conta não pode ser nulo.");
        validarConta(conta);

        // Valida se o cliente associado à conta existe
        Integer clienteId = conta.getCliente().getId();
        clienteDao.buscarPorId(clienteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente com ID " + clienteId + " não encontrado para associar à conta."));

        try {
            // O método salvar agora retorna o ID da conta criada
            Integer idNovaConta = contaDao.salvar(conta);
            conta.setId(idNovaConta); // Atribui o ID gerado ao objeto
            logger.info("Serviço: Conta ID {} para o cliente ID {} foi criada com sucesso.", idNovaConta, conta.getCliente().getId());
            return conta;
        } catch (RepositorioException ex) {
            logger.error("Serviço: Erro ao tentar criar conta para o cliente ID {}: {}", conta.getCliente().getId(), ex.getMessage());
            throw ex;
        }
    }

    /**
     * Busca uma conta pelo seu ID.
     * @param id O ID da conta.
     * @return A conta encontrada.
     * @throws EntidadeNaoEncontradaException se a conta não for encontrada.
     */
    public Conta buscarPorId(Integer id) {
        Objects.requireNonNull(id, "O ID da conta não pode ser nulo.");
        logger.debug("Serviço: Buscando conta com ID: {}", id);

        return contaDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta com ID " + id + " não encontrada."));
    }

    /**
     * Lista todas as contas de um cliente específico.
     * @param idCliente O ID do cliente.
     * @return Uma lista com as contas do cliente.
     */
    public List<Conta> buscarContasPorCliente(Integer idCliente) {
        Objects.requireNonNull(idCliente, "O ID do cliente não pode ser nulo.");
        logger.debug("Serviço: Buscando contas para o cliente ID: {}", idCliente);
        return contaDao.buscarPorCliente(idCliente);
    }

    /**
     * Valida os campos obrigatórios de uma conta.
     * @param conta A conta a ser validada.
     */
    private void validarConta(Conta conta) {
        if (conta.getNumero() == null || conta.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("O número da conta não pode ser vazio.");
        }
        if (conta.getAgencia() == null || conta.getAgencia().trim().isEmpty()) {
            throw new IllegalArgumentException("A agência da conta não pode ser vazia.");
        }
        if (conta.getCliente() == null || conta.getCliente().getId() == null) {
            throw new IllegalArgumentException("A conta deve estar associada a um cliente com ID válido.");
        }
    }
}
