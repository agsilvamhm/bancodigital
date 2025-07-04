package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.Repository.ClienteDao;
import com.agsilvamhm.bancodigital.Repository.ContaDao;
import com.agsilvamhm.bancodigital.Repository.MovimentacaoDao;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ContaService {

    // ... (Logger e Construtor permanecem os mesmos) ...
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
            // AJUSTE AQUI: Validar que a taxa de manutenção não é nula para conta corrente.
            if (request.taxaManutencao() == null) {
                throw new IllegalArgumentException("A taxa de manutenção é obrigatória para Conta Corrente.");
            }
            ContaCorrente cc = new ContaCorrente();
            cc.setTaxaManutencaoMensal(request.taxaManutencao());
            novaConta = cc;

        } else if (TipoConta.POUPANCA.equals(request.tipoConta())) {
            // AJUSTE AQUI: Validar que a taxa de rendimento não é nula para conta poupança.
            if (request.taxaRendimento() == null) {
                throw new IllegalArgumentException("A taxa de rendimento é obrigatória para Conta Poupança.");
            }
            ContaPoupanca cp = new ContaPoupanca();
            cp.setTaxaRendimentoMensal(request.taxaRendimento());
            novaConta = cp;

        } else {
            throw new IllegalArgumentException("Tipo de conta inválido: " + request.tipoConta());
        }

        novaConta.setCliente(cliente);
        novaConta.setNumero(request.numero());
        novaConta.setAgencia(request.agencia());
        novaConta.setSaldo(BigDecimal.ZERO);

        try {
            Conta contaSalva = contaDao.salvar(novaConta);
            logger.info("Serviço: Conta ID {} para o cliente {} foi criada com sucesso.", contaSalva.getId(), cliente.getNome());
            return contaSalva;
        } catch (DataAccessException ex) {
            logger.error("Serviço: Erro de persistência ao tentar criar conta.", ex);
            throw new RepositorioException("Erro ao salvar a conta no banco de dados.", ex);
        }
    }

    // ... O resto da classe (buscarPorId, buscarPorNumero, listar, atualizar, validar) está correto ...
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
        // 1. Reutiliza o método buscarPorId que já trata o caso de não encontrar a conta.
        Conta conta = this.buscarPorId(id);

        // 2. Retorna apenas o saldo do objeto encontrado.
        return conta.getSaldo();
    }

    @Transactional
    public Movimentacao realizarTransferencia(Long idContaOrigem, TransferenciaRequestDTO request) {
        // 1. Validações de negócio
        if (request.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor da transferência deve ser positivo.");
        }

        // 2. Busca as contas de origem e destino
        Conta contaOrigem = buscarPorId(idContaOrigem);
        Conta contaDestino = contaDao.buscarPorNumero(request.contaDestinoNumero())
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Conta de destino com número " + request.contaDestinoNumero() + " não encontrada."));

        if (contaOrigem.getId().equals(contaDestino.getId())) {
            throw new RegraNegocioException("A conta de origem e destino não podem ser a mesma.");
        }

        // 3. Verifica o saldo
        if (contaOrigem.getSaldo().compareTo(request.valor()) < 0) {
            throw new RegraNegocioException("Saldo insuficiente na conta de origem.");
        }

        // 4. Realiza a operação de débito e crédito
        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(request.valor()));
        contaDestino.setSaldo(contaDestino.getSaldo().add(request.valor()));

        // 5. Atualiza as duas contas no banco de dados
        contaDao.atualizar(contaOrigem);
        contaDao.atualizar(contaDestino);

        // 6. Cria e salva o registro da movimentação
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(TipoMovimentacao.TRANSFERENCIA);
        movimentacao.setValor(request.valor().doubleValue()); // Ajuste de BigDecimal para double
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setContaOrigem(contaOrigem);
        movimentacao.setContaDestino(contaDestino);
        movimentacao.setDescricao(request.descricao());

        movimentacaoDao.salvar(movimentacao);

        logger.info("Transferência de R$ {} da conta #{} para #{} realizada com sucesso.",
                request.valor(), contaOrigem.getNumero(), contaDestino.getNumero());

        // Retorna o objeto da movimentação como um "recibo" da transação.
        return movimentacao;
    }

    @Transactional
    public Movimentacao realizarDeposito(Long idConta, DepositoRequestDTO request) {
        // 1. Busca a conta que receberá o depósito.
        // O método buscarPorId já lança uma exceção se a conta não for encontrada.
        Conta conta = buscarPorId(idConta);

        // 2. Adiciona o valor ao saldo da conta.
        BigDecimal novoSaldo = conta.getSaldo().add(request.valor());
        conta.setSaldo(novoSaldo);

        // 3. Atualiza a conta no banco de dados.
        contaDao.atualizar(conta);

        // 4. Cria e salva o registro da movimentação de depósito.
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(TipoMovimentacao.DEPOSITO);
        movimentacao.setValor(request.valor().doubleValue());
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setContaOrigem(null); // Depósitos não têm uma conta de origem no sistema.
        movimentacao.setContaDestino(conta);
        movimentacao.setDescricao(request.descricao() != null ? request.descricao() : "Depósito em conta");

        // Reutilizamos o MovimentacaoDao que já existe.
        movimentacaoDao.salvar(movimentacao);

        logger.info("Depósito de R$ {} na conta #{} realizado com sucesso.", request.valor(), conta.getNumero());

        // Retorna a movimentação como "recibo".
        return movimentacao;
    }

    @Transactional
    public Movimentacao realizarSaque(Long idConta, OperacaoContaDTO request) {
        // 1. Busca a conta da qual o saque será feito.
        Conta conta = buscarPorId(idConta);

        // 2. VALIDAÇÃO MAIS IMPORTANTE: Verificar se há saldo suficiente.
        if (conta.getSaldo().compareTo(request.valor()) < 0) {
            throw new RegraNegocioException("Saldo insuficiente para realizar o saque.");
        }

        // 3. Debita o valor do saldo da conta.
        BigDecimal novoSaldo = conta.getSaldo().subtract(request.valor());
        conta.setSaldo(novoSaldo);

        // 4. Atualiza a conta no banco de dados.
        contaDao.atualizar(conta);

        // 5. Cria e salva o registro da movimentação de saque.
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(TipoMovimentacao.SAQUE); // Certifique-se que seu enum tem SAQUE
        movimentacao.setValor(request.valor().doubleValue());
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setContaOrigem(conta);
        movimentacao.setContaDestino(null); // Saques não têm conta de destino.
        movimentacao.setDescricao(request.descricao() != null ? request.descricao() : "Saque em conta");

        // Reutilizamos o MovimentacaoDao. A correção que fizemos para depósitos já o preparou para isso.
        movimentacaoDao.salvar(movimentacao);

        logger.info("Saque de R$ {} da conta #{} realizado com sucesso.", request.valor(), conta.getNumero());

        return movimentacao;
    }

    @Transactional
    public Movimentacao realizarPix(Long idContaOrigem, PixRequestDTO request) {
        // 1. Busca a conta de origem
        Conta contaOrigem = buscarPorId(idContaOrigem);

        // 2. RESOLUÇÃO DA CHAVE PIX: Busca o cliente de destino pelo CPF (nossa Chave Pix)
        Cliente clienteDestino = clienteDao.buscarPorCpf(request.chavePix())
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Nenhuma conta encontrada para a Chave PIX (CPF) fornecida."));

        // 3. Busca a primeira conta associada a esse cliente de destino.
        // Em um sistema real, poderia haver uma lógica para escolher a conta principal.
        List<Conta> contasDestino = contaDao.buscarPorClienteId(clienteDestino.getId());
        if (contasDestino.isEmpty()) {
            throw new EntidadeNaoEncontradaException(
                    "O destinatário da Chave PIX não possui uma conta ativa.");
        }
        Conta contaDestino = contasDestino.get(0); // Pega a primeira conta encontrada

        // 4. Validações de negócio (saldo, mesma conta, etc.)
        if (contaOrigem.getId().equals(contaDestino.getId())) {
            throw new RegraNegocioException("A conta de origem e destino não podem ser a mesma.");
        }
        if (contaOrigem.getSaldo().compareTo(request.valor()) < 0) {
            throw new RegraNegocioException("Saldo insuficiente para realizar o PIX.");
        }

        // 5. Realiza a operação de débito e crédito
        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(request.valor()));
        contaDestino.setSaldo(contaDestino.getSaldo().add(request.valor()));

        // 6. Atualiza as duas contas no banco de dados
        contaDao.atualizar(contaOrigem);
        contaDao.atualizar(contaDestino);

        // 7. Cria e salva o registro da movimentação
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(TipoMovimentacao.PIX); // Certifique-se que seu enum tem PIX
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

        // 1. Primeiro, garantimos que a conta existe.
        //    O método buscarPorId já lança EntidadeNaoEncontradaException se não achar.
        this.buscarPorId(idConta);

        // 2. Se a conta existe, chamamos o DAO para buscar as movimentações.
        List<Movimentacao> movimentacoes = movimentacaoDao.buscarPorContaId(idConta);

        logger.info("Encontradas {} movimentações para a conta ID: {}", movimentacoes.size(), idConta);
        return movimentacoes;
    }

}