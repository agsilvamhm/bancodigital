package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.controller.exception.CpfDuplicadoException;
import com.agsilvamhm.bancodigital.controller.exception.CpfInvalidoException;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.dao.EnderecoDao;
import com.agsilvamhm.bancodigital.entity.Cliente;
import com.agsilvamhm.bancodigital.dao.ClienteDao;

import com.agsilvamhm.bancodigital.entity.Endereco;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteDao clienteDao;
    private final EnderecoDao enderecoDao;

    @Autowired
    public ClienteService(ClienteDao clienteDao, EnderecoDao enderecoDao) {
        this.clienteDao = clienteDao;
        this.enderecoDao = enderecoDao;
    }

    @Transactional
    public Cliente criarCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "O objeto cliente não pode ser nulo.");
        Objects.requireNonNull(cliente.getEndereco(), "O cliente deve possuir um endereço.");
        validarCliente(cliente);

        try {

            Endereco endereco = cliente.getEndereco();
            Integer idEndereco = enderecoDao.salvar(endereco);
            endereco.setId(idEndereco);

            clienteDao.salvar(cliente);
            logger.info("Serviço: Cliente com CPF {} foi criado com sucesso.", cliente.getCpf());

            return cliente;
        } catch (CpfDuplicadoException | RepositorioException ex) {
            logger.error("Serviço: Erro ao tentar criar cliente com CPF {}: {}", cliente.getCpf(), ex.getMessage());

            throw ex;
        }
    }

    public Cliente buscarPorId(Integer id) {
        Objects.requireNonNull(id, "O ID do cliente não pode ser nulo.");
        logger.debug("Serviço: Buscando cliente com ID: {}", id);

        return clienteDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente com ID " + id + " não encontrado."));
    }

    public List<Cliente> listarTodos() {
        logger.debug("Serviço: Listando todos os clientes.");
        return clienteDao.listarTodos();
    }

    @Transactional
    public Cliente atualizarCliente(Integer id, Cliente clienteAtualizado) {
        Objects.requireNonNull(id, "O ID do cliente não pode ser nulo.");
        Objects.requireNonNull(clienteAtualizado, "O objeto cliente não pode ser nulo.");

        Cliente clienteExistente = buscarPorId(id);

        validarCliente(clienteAtualizado);

        Endereco enderecoParaAtualizar = clienteAtualizado.getEndereco();

        enderecoParaAtualizar.setId(clienteExistente.getEndereco().getId());
        enderecoDao.atualizar(enderecoParaAtualizar);

        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setCpf(clienteAtualizado.getCpf());
        clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());
        clienteExistente.setCategoria(clienteAtualizado.getCategoria());
        clienteExistente.setEndereco(enderecoParaAtualizar); // Associa o endereço atualizado

        clienteDao.atualizar(clienteExistente);
        logger.info("Serviço: Cliente com ID {} foi atualizado com sucesso.", id);

        return clienteExistente;
    }

    @Transactional
    public void deletarPorId(Integer id) {
        Objects.requireNonNull(id, "O ID do cliente não pode ser nulo.");

        Cliente cliente = buscarPorId(id);

        Integer idEndereco = cliente.getEndereco().getId();

        clienteDao.deletar(id);

        enderecoDao.deletar(idEndereco);

        logger.info("Serviço: Cliente com ID {} e endereço associado ID {} foram deletados com sucesso.", id, idEndereco);
    }

    private void validarCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do cliente não pode ser vazio.");
        }
        if(!validarCpf(cliente.getCpf())){
            throw new CpfInvalidoException("CPF inválido!");
        }
    }

    private boolean validarCpf(String cpf){
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * (10 - i);
        }
        int resto = 11 - (soma % 11);
        int digito1 = (resto >= 10) ? 0 : resto;

        if ((cpf.charAt(9) - '0') != digito1) {
            return false;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * (11 - i);
        }
        resto = 11 - (soma % 11);
        int digito2 = (resto >= 10) ? 0 : resto;

        if ((cpf.charAt(10) - '0') != digito2) {
            return false;
        }
        return true;
    }
}
