package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.Repository.CartaoDao;
import com.agsilvamhm.bancodigital.Repository.ContaDao;
import com.agsilvamhm.bancodigital.Repository.MovimentacaoDao;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class CartaoService {

    private static final Logger logger = LoggerFactory.getLogger(CartaoService.class);

    private final CartaoDao cartaoDao;
    private final ContaDao contaDao;
    // Supondo que exista um MovimentacaoDao para registrar transações de débito
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

        // Lógica para criar um novo cartão
        Cartao novoCartao = new Cartao();
        novoCartao.setConta(conta);
        novoCartao.setTipoCartao(request.tipoCartao());
        novoCartao.setNomeTitular(conta.getCliente().getNome());
        novoCartao.setAtivo(true);

        // Gerar dados do cartão (em um sistema real, seriam mais complexos)
        novoCartao.setNumero("5" + UUID.randomUUID().toString().substring(0, 18).replaceAll("[^0-9]", ""));
        novoCartao.setDataValidade(LocalDate.now().plusYears(5));
        novoCartao.setCvv(String.valueOf((int) (Math.random() * 900) + 100));

        // Em um sistema real, a senha seria hasheada aqui antes de salvar
        novoCartao.setSenha(request.senha());

        if (TipoCartao.CREDITO.equals(request.tipoCartao())) {
            novoCartao.setLimiteCredito(request.limiteCredito());
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

        // Validar senha (em um sistema real, compararia hashes)
        if (!cartao.getSenha().equals(request.senha())) {
            throw new RegraNegocioException("Senha do cartão incorreta.");
        }

        if (TipoCartao.CREDITO.equals(cartao.getTipoCartao())) {
            // Lógica de pagamento com crédito
            // Esta é uma simplificação. Um sistema real teria uma tabela de faturas e movimentações de cartão.
            BigDecimal limiteDisponivel = cartao.getLimiteCredito(); // Simplificação
            if (limiteDisponivel.compareTo(request.valor()) < 0) {
                throw new RegraNegocioException("Limite de crédito insuficiente.");
            }
            logger.info("Pagamento de R$ {} no crédito para o cartão ID {} aprovado.", request.valor(), cartaoId);
            // Aqui você atualizaria o saldo utilizado do cartão ou adicionaria a uma fatura.

        } else if (TipoCartao.DEBITO.equals(cartao.getTipoCartao())) {
            // Lógica de pagamento com débito
            Conta conta = contaDao.buscarPorId(cartao.getConta().getId().longValue()).get();
            if (conta.getSaldo().compareTo(request.valor()) < 0) {
                throw new RegraNegocioException("Saldo insuficiente na conta.");
            }
            if (cartao.getLimiteDiarioDebito() != null && request.valor().compareTo(cartao.getLimiteDiarioDebito()) > 0) {
                throw new RegraNegocioException("Valor excede o limite diário de transação.");
            }

            // Debita da conta e registra movimentação
            conta.setSaldo(conta.getSaldo().subtract(request.valor()));
            contaDao.atualizar(conta);

            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setTipo(TipoMovimentacao.SAQUE); // Ou um tipo específico "COMPRA_DEBITO"
            movimentacao.setValor(request.valor().doubleValue());
            movimentacao.setContaOrigem(conta);
            movimentacao.setDescricao(request.descricao());
            movimentacaoDao.salvar(movimentacao);

            logger.info("Pagamento de R$ {} no débito para o cartão ID {} aprovado.", request.valor(), cartaoId);
        }
    }

    @Transactional
    public void alterarStatus(Integer cartaoId, boolean novoStatus) {
        Cartao cartao = buscarPorId(cartaoId); // Garante que o cartão existe
        cartaoDao.atualizarStatus(cartao.getId(), novoStatus);
        logger.info("Status do cartão ID {} alterado para {}.", cartaoId, novoStatus ? "ATIVO" : "INATIVO");
    }
}
