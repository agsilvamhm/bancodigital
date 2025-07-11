package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.repository.ClienteDao;
import com.agsilvamhm.bancodigital.repository.ContaDao;
import com.agsilvamhm.bancodigital.repository.MovimentacaoDao;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RegraNegocioException;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.model.*;
import com.agsilvamhm.bancodigital.model.dto.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ContaService {

    private static final Logger logger = LoggerFactory.getLogger(ContaService.class);

    private final ContaDao contaDao;
    private final ClienteDao clienteDao;
    private final MovimentacaoDao movimentacaoDao;

    @Autowired
    public ContaService(ContaDao contaDao, ClienteDao clienteDao, MovimentacaoDao movimentacaoDao) {
        this.contaDao = contaDao;
        this.clienteDao = clienteDao;
        this.movimentacaoDao = movimentacaoDao;
    }

    @Transactional
    public Conta criarConta(CriarContaRequest request) {
        Objects.requireNonNull(request, "A requisição para criar conta não pode ser nula.");
        validarNovaConta(request);

        Cliente cliente = clienteDao.buscarPorId(request.clienteId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente com ID " + request.clienteId() + " não encontrado."));

        Conta novaConta;

        if (TipoConta.CORRENTE.equals(request.tipoConta())) {
            novaConta = new ContaCorrente();
        } else if (TipoConta.POUPANCA.equals(request.tipoConta())) {
            novaConta = new ContaPoupanca();
        } else {
            throw new IllegalArgumentException("Tipo de conta inválido: " + request.tipoConta());
        }

        novaConta.setCliente(cliente);
        novaConta.setNumero(request.numero());
        novaConta.setAgencia(request.agencia());
        novaConta.setSaldo(BigDecimal.ZERO); // Contas novas começam com saldo zero.

        try {
            Conta contaSalva = contaDao.salvar(novaConta);
            logger.info("Serviço: Conta ID {} para o cliente {} foi criada com sucesso.", contaSalva.getId(), cliente.getNome());
            return contaSalva;
        } catch (DataAccessException ex) {
            logger.error("Serviço: Erro de persistência ao tentar criar conta.", ex);
            throw new RepositorioException("Erro ao salvar a conta no banco de dados.", ex);
        }
    }

    public Conta buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID da conta não pode ser nulo.");
        return contaDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta com ID " + id + " não encontrada."));
    }

    public Conta buscarPorNumero(String numero) {
        Objects.requireNonNull(numero, "O número da conta não pode ser nulo.");
        return contaDao.buscarPorNumero(numero)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta com número " + numero + " não encontrada."));
    }

    public List<Conta> listarTodasContasCorrente() {
        return contaDao.listarContasCorrente();
    }

    public List<Conta> listarTodasContasPoupanca() {
        return contaDao.listarContasPoupanca();
    }

    @Transactional
    public void atualizarConta(Conta conta) {
        contaDao.atualizar(conta);
    }

    private void validarNovaConta(CriarContaRequest request) {
        if (request.numero() == null || request.numero().trim().isEmpty()) {
            throw new IllegalArgumentException("O número da conta não pode ser vazio.");
        }
        if (request.agencia() == null || request.agencia().trim().isEmpty()) {
            throw new IllegalArgumentException("A agência da conta não pode ser vazia.");
        }
        if (contaDao.buscarPorNumero(request.numero()).isPresent()) {
            throw new RegraNegocioException("Uma conta com o número '" + request.numero() + "' já existe.");
        }
    }

    public BigDecimal consultarSaldo(Long id) {
        logger.info("Consultando saldo para a conta ID: {}", id);
        Conta conta = this.buscarPorId(id);
        return conta.getSaldo();
    }

    @Transactional
    public Movimentacao realizarTransferencia(Long idContaOrigem, TransferenciaRequestDTO request) {
        if (request.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor da transferência deve ser positivo.");
        }
        Conta contaOrigem = buscarPorId(idContaOrigem);
        Conta contaDestino = contaDao.buscarPorNumero(request.contaDestinoNumero())
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Conta de destino com número " + request.contaDestinoNumero() + " não encontrada."));
        if (contaOrigem.getId().equals(contaDestino.getId())) {
            throw new RegraNegocioException("A conta de origem e destino não podem ser a mesma.");
        }
        if (contaOrigem.getSaldo().compareTo(request.valor()) < 0) {
            throw new RegraNegocioException("Saldo insuficiente na conta de origem.");
        }
        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(request.valor()));
        contaDestino.setSaldo(contaDestino.getSaldo().add(request.valor()));
        contaDao.atualizar(contaOrigem);
        contaDao.atualizar(contaDestino);
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(TipoMovimentacao.TRANSFERENCIA);
        movimentacao.setValor(request.valor().doubleValue());
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setContaOrigem(contaOrigem);
        movimentacao.setContaDestino(contaDestino);
        movimentacao.setDescricao(request.descricao());
        movimentacaoDao.salvar(movimentacao);
        logger.info("Transferência de R$ {} da conta #{} para #{} realizada com sucesso.",
                request.valor(), contaOrigem.getNumero(), contaDestino.getNumero());
        return movimentacao;
    }

    @Transactional
    public Movimentacao realizarDeposito(Long idConta, DepositoRequestDTO request) {
        Conta conta = buscarPorId(idConta);
        BigDecimal novoSaldo = conta.getSaldo().add(request.valor());
        conta.setSaldo(novoSaldo);
        contaDao.atualizar(conta);
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(TipoMovimentacao.DEPOSITO);
        movimentacao.setValor(request.valor().doubleValue());
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setContaOrigem(null);
        movimentacao.setContaDestino(conta);
        movimentacao.setDescricao(request.descricao() != null ? request.descricao() : "Depósito em conta");
        movimentacaoDao.salvar(movimentacao);
        logger.info("Depósito de R$ {} na conta #{} realizado com sucesso.", request.valor(), conta.getNumero());
        return movimentacao;
    }

    @Transactional
    public Movimentacao realizarSaque(Long idConta, OperacaoContaDTO request) {
        Conta conta = buscarPorId(idConta);
        if (conta.getSaldo().compareTo(request.valor()) < 0) {
            throw new RegraNegocioException("Saldo insuficiente para realizar o saque.");
        }
        BigDecimal novoSaldo = conta.getSaldo().subtract(request.valor());
        conta.setSaldo(novoSaldo);
        contaDao.atualizar(conta);
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(TipoMovimentacao.SAQUE);
        movimentacao.setValor(request.valor().doubleValue());
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setContaOrigem(conta);
        movimentacao.setContaDestino(null);
        movimentacao.setDescricao(request.descricao() != null ? request.descricao() : "Saque em conta");
        movimentacaoDao.salvar(movimentacao);
        logger.info("Saque de R$ {} da conta #{} realizado com sucesso.", request.valor(), conta.getNumero());
        return movimentacao;
    }

    @Transactional
    public Movimentacao realizarPix(Long idContaOrigem, PixRequestDTO request) {
        Conta contaOrigem = buscarPorId(idContaOrigem);
        Cliente clienteDestino = clienteDao.buscarPorCpf(request.chavePix())
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Nenhuma conta encontrada para a Chave PIX (CPF) fornecida."));
        List<Conta> contasDestino = contaDao.buscarPorClienteId(clienteDestino.getId());
        if (contasDestino.isEmpty()) {
            throw new EntidadeNaoEncontradaException(
                    "O destinatário da Chave PIX não possui uma conta ativa.");
        }
        Conta contaDestino = contasDestino.get(0);
        if (contaOrigem.getId().equals(contaDestino.getId())) {
            throw new RegraNegocioException("A conta de origem e destino não podem ser a mesma.");
        }
        if (contaOrigem.getSaldo().compareTo(request.valor()) < 0) {
            throw new RegraNegocioException("Saldo insuficiente para realizar o PIX.");
        }
        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(request.valor()));
        contaDestino.setSaldo(contaDestino.getSaldo().add(request.valor()));
        contaDao.atualizar(contaOrigem);
        contaDao.atualizar(contaDestino);
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(TipoMovimentacao.PIX);
        movimentacao.setValor(request.valor().doubleValue());
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setContaOrigem(contaOrigem);
        movimentacao.setContaDestino(contaDestino);
        movimentacao.setDescricao(request.descricao());
        movimentacaoDao.salvar(movimentacao);
        logger.info("PIX de R$ {} da conta #{} para a Chave Pix '{}' realizado com sucesso.",
                request.valor(), contaOrigem.getNumero(), request.chavePix());
        return movimentacao;
    }

    public List<Movimentacao> listarMovimentacoesPorConta(Long idConta) {
        logger.info("Buscando extrato para a conta ID: {}", idConta);
        this.buscarPorId(idConta);
        List<Movimentacao> movimentacoes = movimentacaoDao.buscarPorContaId(idConta);
        logger.info("Encontradas {} movimentações para a conta ID: {}", movimentacoes.size(), idConta);
        return movimentacoes;
    }

    @Transactional
    public Movimentacao aplicarTaxaManutencao(Long idConta) {
        logger.info("Iniciando aplicação de taxa de manutenção para a conta ID: {}", idConta);
        Conta conta = this.buscarPorId(idConta);
        if (!(conta instanceof ContaCorrente)) {
            throw new RegraNegocioException("A taxa de manutenção só pode ser aplicada a Contas Corrente.");
        }
        ContaCorrente contaCorrente = (ContaCorrente) conta;
        Cliente cliente = contaCorrente.getCliente();
        if (cliente == null || cliente.getCategoria() == null) {
            throw new RegraNegocioException("A conta não possui um cliente ou categoria associada.");
        }
        CategoriaCliente categoria = cliente.getCategoria();
        BigDecimal taxa = categoria.getTaxaManutencao();
        if (taxa.compareTo(BigDecimal.ZERO) <= 0) {
            logger.info("Cliente da conta #{} é da categoria {} e está isento de taxa.", conta.getNumero(), categoria.getDescricao());
            return null;
        }
        if (contaCorrente.getSaldo().compareTo(taxa) < 0) {
            throw new RegraNegocioException("Saldo insuficiente para cobrança da taxa de manutenção.");
        }
        contaCorrente.setSaldo(contaCorrente.getSaldo().subtract(taxa));
        contaDao.atualizar(contaCorrente);
        logger.info("Saldo da conta #{} atualizado após cobrança de taxa de R$ {}.", conta.getNumero(), taxa);
        Movimentacao movimentacaoTaxa = new Movimentacao();
        movimentacaoTaxa.setTipo(TipoMovimentacao.TAXA_MANUTENCAO);
        movimentacaoTaxa.setValor(taxa.doubleValue());
        movimentacaoTaxa.setDataHora(LocalDateTime.now());
        movimentacaoTaxa.setDescricao("Cobrança de taxa de manutenção mensal - Categoria: " + categoria.getDescricao());
        movimentacaoTaxa.setContaOrigem(contaCorrente);
        movimentacaoTaxa.setContaDestino(null);
        movimentacaoDao.salvar(movimentacaoTaxa);
        logger.info("Movimentação de taxa registrada para conta #{}.", conta.getNumero());
        return movimentacaoTaxa;
    }

    @Transactional
    public Movimentacao aplicarRendimentos(Long idConta) {
        logger.info("Iniciando aplicação de rendimentos para a conta ID: {}", idConta);
        Conta conta = this.buscarPorId(idConta);
        if (!(conta instanceof ContaPoupanca)) {
            throw new RegraNegocioException("Rendimentos só podem ser aplicados a Contas Poupança.");
        }
        ContaPoupanca contaPoupanca = (ContaPoupanca) conta;
        Cliente cliente = contaPoupanca.getCliente();
        if (cliente == null || cliente.getCategoria() == null) {
            throw new RegraNegocioException("A conta não possui um cliente ou categoria associada.");
        }
        CategoriaCliente categoria = cliente.getCategoria();
        BigDecimal taxaRendimentoMensal = categoria.getTaxaRendimentoMensalEquivalente();
        BigDecimal valorRendimento = contaPoupanca.getSaldo().multiply(taxaRendimentoMensal)
                .setScale(2, RoundingMode.HALF_EVEN);
        if (valorRendimento.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Rendimento para a conta #{} resultou em R$ 0,00. Nenhuma operação realizada.", conta.getNumero());
            return null;
        }
        contaPoupanca.setSaldo(contaPoupanca.getSaldo().add(valorRendimento));
        contaDao.atualizar(contaPoupanca);
        logger.info("Saldo da conta #{} atualizado após aplicação de rendimentos no valor de R$ {}.", conta.getNumero(), valorRendimento);
        Movimentacao movimentacaoRendimento = new Movimentacao();
        movimentacaoRendimento.setTipo(TipoMovimentacao.RENDIMENTO);
        movimentacaoRendimento.setValor(valorRendimento.doubleValue());
        movimentacaoRendimento.setDataHora(LocalDateTime.now());
        movimentacaoRendimento.setDescricao("Crédito de rendimento mensal - Categoria: " + categoria.getDescricao());
        movimentacaoRendimento.setContaOrigem(null);
        movimentacaoRendimento.setContaDestino(contaPoupanca);
        movimentacaoDao.salvar(movimentacaoRendimento);
        logger.info("Movimentação de rendimento registrada para conta #{}.", conta.getNumero());
        return movimentacaoRendimento;
    }
}