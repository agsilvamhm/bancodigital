package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.controller.exception.CpfDuplicadoException;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.entity.Cliente;
import com.agsilvamhm.bancodigital.repository.ClienteDao;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteDao clienteDao;

    public ClienteService(ClienteDao clienteDao) {
        this.clienteDao = clienteDao;
    }

    @Transactional // Garante que a operação seja atômica. Se ocorrer um erro, nada é salvo.
    public Cliente criarCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "O objeto cliente não pode ser nulo.");
        validarCliente(cliente);
        try {
            clienteDao.salvar(cliente);
            logger.info("Serviço: Cliente com CPF {} foi criado com sucesso.", cliente.getCpf());
        } catch (CpfDuplicadoException | RepositorioException ex) {
            logger.error("Serviço: Erro ao tentar criar cliente com CPF {}: {}", cliente.getCpf(), ex.getMessage());
            throw ex;
        }
        return cliente;
    }

    public Cliente buscarPorId(Integer id) {
        Objects.requireNonNull(id, "O ID do cliente não pode ser nulo.");
        logger.debug("Serviço: Buscando cliente com ID: {}", id);
        return clienteDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente com ID " + id + " não encontrado."));
    }

    public List<Cliente> listarTodos() {
        logger.debug("Serviço: Listando todos os clientes.");
        // A exceção RepositorioException do DAO será propagada se ocorrer um erro.
        return clienteDao.listarTodos();
    }

    @Transactional
    public Cliente atualizarCliente(Integer id, Cliente cliente) {
        Objects.requireNonNull(id, "O ID do cliente não pode ser nulo.");
        Objects.requireNonNull(cliente, "O objeto cliente não pode ser nulo.");
        Cliente clienteExistente = buscarPorId(id);

        validarCliente(cliente);

        clienteExistente.setNome(cliente.getNome());
        clienteExistente.setCpf(cliente.getCpf());
        clienteExistente.setDataNascimento(cliente.getDataNascimento());
        clienteExistente.setCategoria(cliente.getCategoria());
        clienteDao.atualizar(clienteExistente);
        logger.info("Serviço: Cliente com ID {} foi atualizado com sucesso.", id);

        return clienteExistente;
    }

    @Transactional
    public void deletarPorId(Integer id) {
        Objects.requireNonNull(id, "O ID do cliente não pode ser nulo.");
        if (!clienteDao.buscarPorId(id).isPresent()) {
            throw new EntidadeNaoEncontradaException("Cliente com ID " + id + " não encontrado para deleção.");
        }
        clienteDao.deletar(id);
        logger.info("Serviço: Cliente com ID {} foi deletado com sucesso.", id);
    }

    private void validarCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do cliente não pode ser vazio.");
        }
    }
}