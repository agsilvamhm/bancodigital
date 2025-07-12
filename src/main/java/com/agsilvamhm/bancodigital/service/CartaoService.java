package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.model.dto.FaturaDTO;
import com.agsilvamhm.bancodigital.repository.CartaoDao;
import com.agsilvamhm.bancodigital.repository.ContaDao;
import com.agsilvamhm.bancodigital.repository.MovimentacaoDao;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RegraNegocioException;
import com.agsilvamhm.bancodigital.model.*;
import com.agsilvamhm.bancodigital.model.dto.EmitirCartaoRequest;
import com.agsilvamhm.bancodigital.model.dto.PagamentoCartaoRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class CartaoService {

    private static final Logger logger = LoggerFactory.getLogger(CartaoService.class);

    private final CartaoDao cartaoDao;
    private final ContaDao contaDao; // Presume que você tem um ContaDao para buscar a Conta e seu Cliente
    private final MovimentacaoDao movimentacaoDao;

    @Autowired
    public CartaoService(CartaoDao cartaoDao, ContaDao contaDao, MovimentacaoDao movimentacaoDao) {
        this.cartaoDao = cartaoDao;
        this.contaDao = contaDao;
        this.movimentacaoDao = movimentacaoDao;
    }

    @Transactional
    public Cartao emitirNovoCartao(EmitirCartaoRequest request) {
        Objects.requireNonNull(request, "A requisição para emitir cartão não pode ser nula.");

        Conta conta = contaDao.buscarPorId(request.contaId().longValue())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta com ID " + request.contaId() + " não encontrada."));

        Cliente cliente = conta.getCliente();
        if (cliente == null || cliente.getCategoria() == null) {
            throw new RegraNegocioException("Cliente ou Categoria do Cliente não definidos para a conta ID: " + conta.getId());
        }
        CategoriaCliente categoriaCliente = cliente.getCategoria();

        Cartao novoCartao = new Cartao();
        novoCartao.setConta(conta);
        novoCartao.setTipoCartao(request.tipoCartao());
        novoCartao.setNomeTitular(conta.getCliente().getNome());
        novoCartao.setAtivo(true);

        novoCartao.setNumero("5" + UUID.randomUUID().toString().substring(0, 18).replaceAll("[^0-9]", ""));
        novoCartao.setDataValidade(LocalDate.now().plusYears(5));
        novoCartao.setCvv(String.valueOf((int) (Math.random() * 900) + 100));

        novoCartao.setSenha(request.senha()); // Lembre-se de hashear a senha no mundo real

        if (TipoCartao.CREDITO.equals(request.tipoCartao())) {
            // Define o limite de crédito com base na CategoriaCliente (preferencial)
            novoCartao.setLimiteCredito(categoriaCliente.getLimiteCreditoPadrao());
            // Opcional: Se a request permitir um limite, você pode adicionar lógica para validar/aplicar
            // if (request.limiteCredito() != null && request.limiteCredito().compareTo(BigDecimal.ZERO) > 0) {
            //    novoCartao.setLimiteCredito(request.limiteCredito());
            // }
            logger.info("Cartão de crédito para categoria {} com limite de R$ {}.", categoriaCliente.getDescricao(), novoCartao.getLimiteCredito());
        } else {
            novoCartao.setLimiteDiarioDebito(request.limiteDiarioDebito());
        }

        Cartao cartaoSalvo = cartaoDao.salvar(novoCartao);
        logger.info("Cartão ID {} emitido com sucesso para a conta ID {}.", cartaoSalvo.getId(), conta.getId());
        return cartaoSalvo;
    }

    public Cartao buscarPorId(Integer id) {
        return cartaoDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cartão com ID " + id + " não encontrado."));
    }

    public List<Cartao> buscarPorContaId(Integer contaId) {
        return cartaoDao.buscarPorContaId(contaId);
    }

    @Transactional
    public void realizarPagamento(Integer cartaoId, PagamentoCartaoRequest request) {
        Cartao cartao = buscarPorId(cartaoId);

        if (!cartao.isAtivo()) {
            throw new RegraNegocioException("O cartão está inativo.");
        }

        // **IMPORTANTE**: No sistema real, a senha armazenada seria um hash e você compararia o hash
        // exemplo: if (!passwordEncoder.matches(request.senha(), cartao.getSenha())) { ... }
        if (!cartao.getSenha().equals(request.senha())) { // Placeholder para demonstração
            throw new RegraNegocioException("Senha do cartão incorreta.");
        }

        if (TipoCartao.CREDITO.equals(cartao.getTipoCartao())) {
            // Lógica para pagamento com crédito
            // Para um cálculo de limite disponível real, você precisaria da fatura atual.
            // Aqui, apenas validamos contra o limite total como simplificação.
            BigDecimal limiteDisponivelAtual = cartao.getLimiteCredito();
            // Lógica para obter o saldo devedor da fatura e subtrair do limite
            // Ex: BigDecimal saldoDevedorFatura = faturaService.getSaldoDevedorAtual(cartaoId);
            //     limiteDisponivelAtual = limiteDisponivelAtual.subtract(saldoDevedorFatura);


            if (limiteDisponivelAtual.compareTo(request.valor()) < 0) {
                throw new RegraNegocioException("Limite de crédito insuficiente.");
            }

            // Registra a movimentação de crédito
            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setTipo(TipoMovimentacao.COMPRA_CREDITO);
            movimentacao.setValor(request.valor().doubleValue());
            movimentacao.setCartao(cartao); // Associa a movimentação ao cartão
            movimentacao.setDescricao(request.descricao());
            movimentacao.setDataHora(LocalDateTime.now()); // Garante que a data/hora é atual
            movimentacaoDao.salvar(movimentacao);

            logger.info("Pagamento de R$ {} no crédito para o cartão ID {} aprovado.", request.valor(), cartaoId);

        } else if (TipoCartao.DEBITO.equals(cartao.getTipoCartao())) {
            // Lógica de pagamento com débito
            Conta conta = contaDao.buscarPorId(cartao.getConta().getId().longValue())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta associada ao cartão não encontrada."));

            if (conta.getSaldo().compareTo(request.valor()) < 0) {
                throw new RegraNegocioException("Saldo insuficiente na conta.");
            }
            if (cartao.getLimiteDiarioDebito() != null && request.valor().compareTo(cartao.getLimiteDiarioDebito()) > 0) {
                throw new RegraNegocioException("Valor excede o limite diário de transação.");
            }

            conta.setSaldo(conta.getSaldo().subtract(request.valor()));
            contaDao.atualizar(conta); // Presume que você tem um método atualizar na ContaDao

            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setTipo(TipoMovimentacao.SAQUE); // Ou TipoMovimentacao.PAGAMENTO_DEBITO
            movimentacao.setValor(request.valor().doubleValue());
            movimentacao.setContaOrigem(conta);
            movimentacao.setDescricao(request.descricao());
            movimentacao.setDataHora(LocalDateTime.now());
            movimentacaoDao.salvar(movimentacao);

            logger.info("Pagamento de R$ {} no débito para o cartão ID {} aprovado.", request.valor(), cartaoId);
        }
    }

    @Transactional
    public void alterarStatus(Integer cartaoId, boolean novoStatus) {
        Cartao cartao = buscarPorId(cartaoId);
        cartaoDao.atualizarStatus(cartao.getId(), novoStatus);
        logger.info("Status do cartão ID {} alterado para {}.", cartaoId, novoStatus ? "ATIVO" : "INATIVO");
    }

    // --- Métodos Adicionais Implementados ---

    @Transactional
    public void atualizarLimiteCredito(Integer cartaoId, BigDecimal novoLimite) {
        Cartao cartao = buscarPorId(cartaoId);
        if (!TipoCartao.CREDITO.equals(cartao.getTipoCartao())) {
            throw new RegraNegocioException("Operação de limite de crédito não aplicável a este tipo de cartão.");
        }
        if (novoLimite.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("O limite de crédito não pode ser negativo.");
        }
        // Opcional: Adicionar validação se o novo limite excede o máximo permitido para a categoria do cliente
        cartaoDao.atualizarLimiteCredito(cartaoId, novoLimite);
        logger.info("Limite de crédito do cartão ID {} alterado para R$ {}.", cartaoId, novoLimite);
    }

    @Transactional
    public void alterarSenha(Integer cartaoId, String novaSenha) {
        Cartao cartao = buscarPorId(cartaoId);
        // **IMPORTANTE**: Aqui você deve hashear a nova senha antes de salvá-la
        // String novaSenhaHasheada = passwordEncoder.encode(novaSenha);
        String novaSenhaHasheada = novaSenha; // Placeholder
        cartaoDao.atualizarSenha(cartaoId, novaSenhaHasheada);
        logger.info("Senha do cartão ID {} alterada com sucesso.", cartaoId);
    }

    @Transactional
    public void pagarFatura(Integer cartaoId, BigDecimal valorPagamento) {
        Cartao cartao = buscarPorId(cartaoId);
        if (!TipoCartao.CREDITO.equals(cartao.getTipoCartao())) {
            throw new RegraNegocioException("Pagamento de fatura é aplicável apenas para cartões de crédito.");
        }
        if (valorPagamento.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor do pagamento da fatura deve ser positivo.");
        }

        Conta contaPagamento = cartao.getConta(); // A conta associada ao cartão
        if (contaPagamento.getSaldo().compareTo(valorPagamento) < 0) {
            throw new RegraNegocioException("Saldo insuficiente na conta para pagar a fatura.");
        }

        // Lógica real de pagamento de fatura:
        // 1. Diminuir o saldo da conta
        contaPagamento.setSaldo(contaPagamento.getSaldo().subtract(valorPagamento));
        contaDao.atualizar(contaPagamento);

        // 2. Registrar movimentação de pagamento de fatura
        Movimentacao movimentacaoPagamento = new Movimentacao();
        movimentacaoPagamento.setTipo(TipoMovimentacao.PAGAMENTO_FATURA);
        movimentacaoPagamento.setValor(valorPagamento.doubleValue());
        movimentacaoPagamento.setContaOrigem(contaPagamento); // A conta que pagou
        movimentacaoPagamento.setCartao(cartao); // O cartão cuja fatura foi paga
        movimentacaoPagamento.setDescricao("Pagamento de fatura do cartão " + cartao.getNumero());
        movimentacaoPagamento.setDataHora(LocalDateTime.now());
        movimentacaoDao.salvar(movimentacaoPagamento);

        // 3. Atualizar o saldo devedor da fatura (isso envolveria uma entidade Fatura e FaturaService)
        // Por exemplo: faturaService.registrarPagamento(cartaoId, valorPagamento);

        logger.info("Pagamento de fatura de R$ {} para o cartão ID {} realizado com sucesso.", valorPagamento, cartaoId);
    }

    @Transactional
    public void atualizarLimiteDiarioDebito(Integer cartaoId, BigDecimal novoLimite) {
        Cartao cartao = buscarPorId(cartaoId);
        if (!TipoCartao.DEBITO.equals(cartao.getTipoCartao())) {
            throw new RegraNegocioException("Operação de limite diário de débito não aplicável a este tipo de cartão.");
        }
        if (novoLimite.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("O limite diário de débito não pode ser negativo.");
        }
        cartaoDao.atualizarLimiteDiarioDebito(cartaoId, novoLimite);
        logger.info("Limite diário de débito do cartão ID {} alterado para R$ {}.", cartaoId, novoLimite);
    }


    // Método para gerar fatura (já estava na versão anterior)
    public FaturaDTO gerarFaturaMensal(Integer cartaoId, YearMonth mesReferencia) {
        Cartao cartao = buscarPorId(cartaoId);

        if (!TipoCartao.CREDITO.equals(cartao.getTipoCartao())) {
            throw new RegraNegocioException("A fatura é aplicável apenas para cartões de crédito.");
        }

        List<Movimentacao> gastosNoMes = movimentacaoDao.buscarGastosCreditoPorCartaoEMes(cartaoId, mesReferencia);

        BigDecimal totalGasto = gastosNoMes.stream()
                .map(m -> BigDecimal.valueOf(m.getValor()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal limiteCredito = cartao.getLimiteCredito();
        BigDecimal oitentaPorCentoDoLimite = limiteCredito.multiply(new BigDecimal("0.80"));

        BigDecimal taxaUtilizacao = BigDecimal.ZERO;
        if (totalGasto.compareTo(oitentaPorCentoDoLimite) > 0) {
            taxaUtilizacao = totalGasto.multiply(new BigDecimal("0.05"));
            logger.info("Taxa de utilização de R$ {} aplicada para o cartão ID {} no mês {}.",
                    taxaUtilizacao, cartaoId, mesReferencia);
        }

        BigDecimal valorTotalFatura = totalGasto.add(taxaUtilizacao);

        return new FaturaDTO(cartao.getId(), cartao.getNumero(), mesReferencia, totalGasto, taxaUtilizacao, valorTotalFatura, gastosNoMes);
    }
}