package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.SaldoInsuficienteException;
import com.agsilvamhm.bancodigital.dao.ContaDao;
import com.agsilvamhm.bancodigital.dao.MovimentacaoDao;
import com.agsilvamhm.bancodigital.entity.Conta;
import com.agsilvamhm.bancodigital.entity.Movimentacao;
import com.agsilvamhm.bancodigital.entity.TipoMovimentacao;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class MovimentacaoService {

    private static final Logger logger = LoggerFactory.getLogger(MovimentacaoService.class);

    private final MovimentacaoDao movimentacaoDao;
    private final ContaDao contaDao;

    @Autowired
    public MovimentacaoService(MovimentacaoDao movimentacaoDao, ContaDao contaDao) {
        this.movimentacaoDao = movimentacaoDao;
        this.contaDao = contaDao;
    }

    /**
     * Realiza uma transferência (PIX ou TED) entre duas contas.
     * Esta operação é transacional: ou o débito e o crédito são bem-sucedidos, ou ambos são desfeitos.
     *
     * @param idContaOrigem O ID da conta que envia o dinheiro.
     * @param idContaDestino O ID da conta que recebe o dinheiro.
     * @param valor O montante a ser transferido.
     * @param descricao Uma breve descrição da transação.
     * @return O objeto Movimentacao que representa a transação salva.
     */
    @Transactional
    public Movimentacao realizarTransferencia(Integer idContaOrigem, Integer idContaDestino, BigDecimal valor, String descricao) {
        validarInputsTransferencia(idContaOrigem, idContaDestino, valor);

        // Busca as contas envolvidas na transação
        Conta contaOrigem = buscarContaPorId(idContaOrigem);
        Conta contaDestino = buscarContaPorId(idContaDestino);

        // Converte o saldo (double) para BigDecimal para a comparação segura
        BigDecimal saldoOrigem = BigDecimal.valueOf(contaOrigem.getSaldo());

        // Valida se a conta de origem tem saldo suficiente
        if (saldoOrigem.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente na conta " + idContaOrigem + " para realizar a transferência.");
        }

        // Calcula os novos saldos usando BigDecimal
        BigDecimal novoSaldoOrigem = saldoOrigem.subtract(valor);
        BigDecimal novoSaldoDestino = BigDecimal.valueOf(contaDestino.getSaldo()).add(valor);

        // Atualiza os saldos no banco de dados, convertendo de volta para double
        contaDao.atualizarSaldo(idContaOrigem, novoSaldoOrigem.doubleValue());
        contaDao.atualizarSaldo(idContaDestino, novoSaldoDestino.doubleValue());

        // Cria e salva o registro da movimentação
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setContaOrigem(contaOrigem);
        movimentacao.setContaDestino(contaDestino);
        movimentacao.setValor(valor.doubleValue());
        movimentacao.setTipo(TipoMovimentacao.TRANSFERENCIA);
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setDescricao(descricao);

        // Supondo que a DAO foi ajustada para obter os IDs das entidades Conta
        Integer idMovimentacao = movimentacaoDao.salvar(movimentacao);
        movimentacao.setId(idMovimentacao);

        logger.info("Transferência de R$ {} da conta {} para a conta {} realizada com sucesso.", valor, idContaOrigem, idContaDestino);

        return movimentacao;
    }

    /**
     * Realiza um depósito em uma conta específica.
     *
     * @param idContaDestino O ID da conta que receberá o depósito.
     * @param valor O valor a ser depositado.
     * @return O registro da movimentação de depósito.
     */
    @Transactional
    public Movimentacao realizarDeposito(Integer idContaDestino, BigDecimal valor) {
        Objects.requireNonNull(idContaDestino, "ID da conta de destino não pode ser nulo.");
        validarValor(valor);

        Conta contaDestino = buscarContaPorId(idContaDestino);

        BigDecimal novoSaldo = BigDecimal.valueOf(contaDestino.getSaldo()).add(valor);
        contaDao.atualizarSaldo(idContaDestino, novoSaldo.doubleValue());

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setContaOrigem(null); // Depósito não tem origem
        movimentacao.setContaDestino(contaDestino);
        movimentacao.setValor(valor.doubleValue());
        movimentacao.setTipo(TipoMovimentacao.DEPOSITO);
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setDescricao("Depósito em conta");

        Integer idMovimentacao = movimentacaoDao.salvar(movimentacao);
        movimentacao.setId(idMovimentacao);

        logger.info("Depósito de R$ {} na conta {} realizado com sucesso.", valor, idContaDestino);

        return movimentacao;
    }

    /**
     * Realiza um saque de uma conta específica.
     *
     * @param idContaOrigem O ID da conta da qual o saque será efetuado.
     * @param valor O valor a ser sacado.
     * @return O registro da movimentação de saque.
     */
    @Transactional
    public Movimentacao realizarSaque(Integer idContaOrigem, BigDecimal valor) {
        Objects.requireNonNull(idContaOrigem, "ID da conta de origem não pode ser nulo.");
        validarValor(valor);

        Conta contaOrigem = buscarContaPorId(idContaOrigem);

        BigDecimal saldoOrigem = BigDecimal.valueOf(contaOrigem.getSaldo());

        if (saldoOrigem.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente na conta " + idContaOrigem + " para realizar o saque.");
        }

        BigDecimal novoSaldo = saldoOrigem.subtract(valor);
        contaDao.atualizarSaldo(idContaOrigem, novoSaldo.doubleValue());

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setContaOrigem(contaOrigem);
        movimentacao.setContaDestino(null); // Saque não tem destino
        movimentacao.setValor(valor.doubleValue());
        movimentacao.setTipo(TipoMovimentacao.SAQUE);
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setDescricao("Saque em conta");

        Integer idMovimentacao = movimentacaoDao.salvar(movimentacao);
        movimentacao.setId(idMovimentacao);

        logger.info("Saque de R$ {} da conta {} realizado com sucesso.", valor, idContaOrigem);

        return movimentacao;
    }


    /**
     * Busca o extrato (histórico de movimentações) de uma conta.
     *
     * @param idConta O ID da conta a ser consultada.
     * @return Uma lista de todas as movimentações da conta.
     */
    public List<Movimentacao> gerarExtrato(Integer idConta) {
        Objects.requireNonNull(idConta, "O ID da conta não pode ser nulo.");
        // Garante que a conta existe antes de buscar as movimentações
        buscarContaPorId(idConta);
        logger.debug("Serviço: Gerando extrato para a conta ID: {}", idConta);
        return movimentacaoDao.buscarPorConta(idConta);
    }

    /**
     * Busca uma conta pelo ID, encapsulando a chamada ao DAO e o tratamento de exceção.
     */
    private Conta buscarContaPorId(Integer idConta) {
        return contaDao.buscarPorId(idConta)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta com ID " + idConta + " não encontrada."));
    }

    /**
     * Valida os parâmetros de entrada para uma transferência.
     */
    private void validarInputsTransferencia(Integer idContaOrigem, Integer idContaDestino, BigDecimal valor) {
        Objects.requireNonNull(idContaOrigem, "ID da conta de origem não pode ser nulo.");
        Objects.requireNonNull(idContaDestino, "ID da conta de destino não pode ser nulo.");

        if (idContaOrigem.equals(idContaDestino)) {
            throw new IllegalArgumentException("A conta de origem não pode ser a mesma da conta de destino.");
        }

        validarValor(valor);
    }

    /**
     * Valida se o valor da transação é positivo.
     */
    private void validarValor(BigDecimal valor) {
        Objects.requireNonNull(valor, "O valor não pode ser nulo.");
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser positivo.");
        }
    }
}
