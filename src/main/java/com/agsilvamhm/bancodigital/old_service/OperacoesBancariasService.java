package com.agsilvamhm.bancodigital.old_service;

import com.agsilvamhm.bancodigital.model.Conta;
import com.agsilvamhm.bancodigital.old_entity.dto.TransferenciaPixRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OperacoesBancariasService {
  /*  @Autowired
    private ContaDAO contaDAO;

    public BigDecimal exibirSaldo(String numeroConta) {
        Conta conta = contaDAO.buscarPorNumeroConta(numeroConta)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada: " + numeroConta));
        return conta.getSaldo();
    }

    @Transactional // Importante para garantir a atomicidade da transação
    public void transferirViaPix(TransferenciaPixRequest request) {
        if (request.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser positivo.");
        }

        Conta contaOrigem = contaDAO.buscarPorNumeroConta(request.numeroContaOrigem())
                .orElseThrow(() -> new RuntimeException("Conta de origem não encontrada."));
        Conta contaDestino = contaDAO.buscarPorNumeroConta(request.numeroContaDestino())
                .orElseThrow(() -> new RuntimeException("Conta de destino não encontrada."));

        if (contaOrigem.getSaldo().compareTo(request.valor()) < 0) {
            throw new RuntimeException("Saldo insuficiente.");
        }

        // Debita da origem e credita no destino
        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(request.valor()));
        contaDestino.setSaldo(contaDestino.getSaldo().add(request.valor()));

        // Atualiza as duas contas no banco
        contaDAO.atualizar(contaOrigem);
        contaDAO.atualizar(contaDestino);

        // Aqui você também adicionaria a lógica para salvar as transações no extrato
    } */
}
