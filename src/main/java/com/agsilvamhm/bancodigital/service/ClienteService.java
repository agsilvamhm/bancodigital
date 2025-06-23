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

    /**
     * Cria um novo cliente após aplicar as validações de negócio.
     *
     * @param cliente O objeto Cliente a ser criado.
     * @return O Cliente após ser salvo (recomendado, mas mantido void para simplicidade).
     */
    @Transactional // Garante que a operação seja atômica. Se ocorrer um erro, nada é salvo.
    public Cliente criarCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "O objeto cliente não pode ser nulo.");

        // --- Boas Práticas: Centralizar Regras de Negócio no Service ---
        validarCliente(cliente);

        // O tratamento de exceções como CpfDuplicadoException e RepositorioException
        // é delegado para a camada superior (Controller), que irá traduzi-las em respostas HTTP adequadas.
        try {
            clienteDao.salvar(cliente);
            logger.info("Serviço: Cliente com CPF {} foi criado com sucesso.", cliente.getCpf());
        } catch (CpfDuplicadoException | RepositorioException ex) {
            // Apenas logamos e relançamos a exceção para que o Controller a trate.
            logger.error("Serviço: Erro ao tentar criar cliente com CPF {}: {}", cliente.getCpf(), ex.getMessage());
            throw ex;
        }
        return cliente;
    }

    /**
     * Busca um cliente pelo seu ID.
     * @param id O ID do cliente.
     * @return O objeto Cliente encontrado.
     * @throws EntidadeNaoEncontradaException se nenhum cliente for encontrado com o ID fornecido.
     */
    public Cliente buscarPorId(Integer id) {
        Objects.requireNonNull(id, "O ID do cliente não pode ser nulo.");
        logger.debug("Serviço: Buscando cliente com ID: {}", id);

        // --- Boas Práticas: Tratamento de Optional ---
        // Desembrulha o Optional retornado pelo DAO. Se estiver vazio, lança uma exceção
        // clara e específica da camada de negócio/serviço.
        return clienteDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente com ID " + id + " não encontrado."));
    }

    /**
     * Lista todos os clientes.
     * @return Uma lista de todos os clientes.
     */
    public List<Cliente> listarTodos() {
        logger.debug("Serviço: Listando todos os clientes.");
        // A exceção RepositorioException do DAO será propagada se ocorrer um erro.
        return clienteDao.listarTodos();
    }

    /**
     * Atualiza os dados de um cliente existente.
     * @param id O ID do cliente a ser atualizado.
     * @param cliente O objeto Cliente com os novos dados.
     * @return O cliente com seus dados atualizados.
     * @throws EntidadeNaoEncontradaException se o cliente não for encontrado.
     */
    @Transactional
    public Cliente atualizarCliente(Integer id, Cliente cliente) {
        Objects.requireNonNull(id, "O ID do cliente não pode ser nulo.");
        Objects.requireNonNull(cliente, "O objeto cliente não pode ser nulo.");

        // --- Boas Práticas: "Read-then-write" para atualizações ---
        // 1. Garante que o cliente realmente existe antes de tentar atualizar.
        Cliente clienteExistente = buscarPorId(id);

        // 2. Aplica as regras de negócio.
        validarCliente(cliente);

        // 3. Atualiza o objeto existente com os novos dados.
        clienteExistente.setNome(cliente.getNome());
        clienteExistente.setCpf(cliente.getCpf());
        clienteExistente.setDataNascimento(cliente.getDataNascimento());
        clienteExistente.setCategoria(cliente.getCategoria());

        // 4. Persiste a alteração.
        clienteDao.atualizar(clienteExistente);
        logger.info("Serviço: Cliente com ID {} foi atualizado com sucesso.", id);

        return clienteExistente;
    }

    /**
     * Deleta um cliente pelo ID.
     * @param id O ID do cliente a ser deletado.
     * @throws EntidadeNaoEncontradaException se o cliente não for encontrado.
     */
    @Transactional
    public void deletarPorId(Integer id) {
        Objects.requireNonNull(id, "O ID do cliente não pode ser nulo.");
        if (!clienteDao.buscarPorId(id).isPresent()) {
            throw new EntidadeNaoEncontradaException("Cliente com ID " + id + " não encontrado para deleção.");
        }
        clienteDao.deletar(id);
        logger.info("Serviço: Cliente com ID {} foi deletado com sucesso.", id);
    }

    /**
     * Método privado para centralizar as validações de negócio do Cliente.
     * @param cliente O cliente a ser validado.
     */
    private void validarCliente(Cliente cliente) {
        // Exemplo de regra de negócio: o nome não pode estar em branco.
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do cliente não pode ser vazio.");
        }
        // Exemplo de regra de negócio: validação de formato de CPF (usando uma lib externa ou regex)
        // if (!ValidadorCPF.isValid(cliente.getCpf())) {
        //     throw new IllegalArgumentException("O CPF informado é inválido.");
        // }
        // Outras validações...
    }
}