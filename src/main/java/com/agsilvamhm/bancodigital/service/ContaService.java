package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.controller.exception.ContaDuplicadaException;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.dao.ContaCorrenteDao;
import com.agsilvamhm.bancodigital.dao.ContaDao;
import com.agsilvamhm.bancodigital.dao.ContaPoupancaDao;
import com.agsilvamhm.bancodigital.entity.Conta;
import com.agsilvamhm.bancodigital.entity.ContaCorrente;
import com.agsilvamhm.bancodigital.entity.ContaPoupanca;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class ContaService {

    private static final Logger logger = LoggerFactory.getLogger(ContaService.class);

    private final ContaDao contaDao;
    private final ContaCorrenteDao contaCorrenteDao;
    private final ContaPoupancaDao contaPoupancaDao;

    // Injeção de todos os DAOs necessários via construtor
    public ContaService(ContaDao contaDao, ContaCorrenteDao contaCorrenteDao, ContaPoupancaDao contaPoupancaDao) {
        this.contaDao = contaDao;
        this.contaCorrenteDao = contaCorrenteDao;
        this.contaPoupancaDao = contaPoupancaDao;
    }

    // --- MÉTODOS DE CRIAÇÃO (Específicos por tipo) ---

    @Transactional
    public ContaCorrente criarContaCorrente(ContaCorrente conta) {
        Objects.requireNonNull(conta, "O objeto ContaCorrente não pode ser nulo.");
        validarConta(conta);
        if (conta.getTaxaManutencao() == null) {
            throw new IllegalArgumentException("A taxa de manutenção para Conta Corrente não pode ser nula.");
        }

        try {
            contaCorrenteDao.salvar(conta);
            logger.info("Serviço: Conta Corrente para agência/número {}/{} criada com sucesso.", conta.getAgencia(), conta.getNumeroConta());
            return conta;
        } catch (ContaDuplicadaException | RepositorioException ex) {
            logger.error("Serviço: Erro ao tentar criar Conta Corrente {}/{}: {}", conta.getAgencia(), conta.getNumeroConta(), ex.getMessage());
            throw ex;
        }
    }

    @Transactional
    public ContaPoupanca criarContaPoupanca(ContaPoupanca conta) {
        Objects.requireNonNull(conta, "O objeto ContaPoupanca não pode ser nulo.");
        validarConta(conta);
        if (conta.getTaxaRendimento() == null) {
            throw new IllegalArgumentException("A taxa de rendimento para Conta Poupança não pode ser nula.");
        }

        try {
            contaPoupancaDao.salvar(conta);
            logger.info("Serviço: Conta Poupança para agência/número {}/{} criada com sucesso.", conta.getAgencia(), conta.getNumeroConta());
            return conta;
        } catch (ContaDuplicadaException | RepositorioException ex) {
            logger.error("Serviço: Erro ao tentar criar Conta Poupança {}/{}: {}", conta.getAgencia(), conta.getNumeroConta(), ex.getMessage());
            throw ex;
        }
    }

    // --- MÉTODOS DE LEITURA (Podem ser genéricos ou específicos) ---

    /**
     * Busca os dados básicos de uma conta por ID, sem detalhes de tipo.
     */
    public Conta buscarContaPorId(Integer id) {
        Objects.requireNonNull(id, "O ID da conta não pode ser nulo.");
        logger.debug("Serviço: Buscando conta com ID: {}", id);
        return contaDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta com ID " + id + " não encontrada."));
    }

    /**
     * Busca uma Conta Corrente completa, incluindo seus dados específicos.
     */
    public ContaCorrente buscarContaCorrentePorId(Integer id) {
        Objects.requireNonNull(id, "O ID da conta não pode ser nulo.");
        logger.debug("Serviço: Buscando Conta Corrente com ID: {}", id);
        return contaCorrenteDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta Corrente com ID " + id + " não encontrada."));
    }

    /**
     * Busca uma Conta Poupança completa, incluindo seus dados específicos.
     */
    public ContaPoupanca buscarContaPoupancaPorId(Integer id) {
        Objects.requireNonNull(id, "O ID da conta não pode ser nulo.");
        logger.debug("Serviço: Buscando Conta Poupança com ID: {}", id);
        return contaPoupancaDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta Poupança com ID " + id + " não encontrada."));
    }

    /**
     * Lista os dados básicos de todas as contas.
     */
    public List<Conta> listarTodasContas() {
        logger.debug("Serviço: Listando todas as contas.");
        return contaDao.listarTodos();
    }

    // --- MÉTODOS DE ATUALIZAÇÃO (Específicos por tipo) ---

    @Transactional
    public ContaCorrente atualizarContaCorrente(Integer id, ContaCorrente contaAtualizacao) {
        Objects.requireNonNull(id, "O ID da conta não pode ser nulo.");
        Objects.requireNonNull(contaAtualizacao, "O objeto ContaCorrente não pode ser nulo.");

        // Busca a entidade existente para garantir que ela existe antes de atualizar
        ContaCorrente contaExistente = buscarContaCorrentePorId(id);

        // Valida os dados básicos da conta
        validarConta(contaAtualizacao);
        if (contaAtualizacao.getTaxaManutencao() == null) {
            throw new IllegalArgumentException("A taxa de manutenção para Conta Corrente não pode ser nula.");
        }

        // Atualiza os campos da entidade existente com os novos valores
        contaExistente.setSaldo(contaAtualizacao.getSaldo()); // Exemplo, outros campos podem ser atualizáveis
        contaExistente.setTaxaManutencao(contaAtualizacao.getTaxaManutencao());

        contaCorrenteDao.atualizar(contaExistente);
        logger.info("Serviço: Conta Corrente com ID {} foi atualizada com sucesso.", id);

        return contaExistente;
    }

    // O método para atualizar ContaPoupanca seguiria o mesmo padrão do de ContaCorrente.

    // --- MÉTODO DE DELEÇÃO (Genérico) ---

    @Transactional
    public void deletarConta(Integer id) {
        Objects.requireNonNull(id, "O ID da conta não pode ser nulo.");
        // Verifica a existência usando o DAO genérico antes de deletar
        if (!contaDao.buscarPorId(id).isPresent()) {
            throw new EntidadeNaoEncontradaException("Conta com ID " + id + " não encontrada para deleção.");
        }
        contaDao.deletar(id);
        logger.info("Serviço: Conta com ID {} foi deletada com sucesso.", id);
    }

    // --- MÉTODO DE VALIDAÇÃO (Privado) ---

    private void validarConta(Conta conta) {
        if (conta.getIdCliente() == null) {
            throw new IllegalArgumentException("A conta deve estar associada a um cliente (ID do cliente não pode ser nulo).");
        }
        if (conta.getAgencia() == null || conta.getAgencia().trim().isEmpty()) {
            throw new IllegalArgumentException("O número da agência não pode ser vazio.");
        }
        if (conta.getNumeroConta() == null || conta.getNumeroConta().trim().isEmpty()) {
            throw new IllegalArgumentException("O número da conta não pode ser vazio.");
        }
        if (conta.getSaldo() == null || conta.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O saldo não pode ser nulo ou negativo.");
        }
    }
}
