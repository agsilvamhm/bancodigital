package com.agsilvamhm.bancodigital.old_service;

import com.agsilvamhm.bancodigital.model.CategoriaCliente;
import com.agsilvamhm.bancodigital.model.Conta;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TarefasAgendadasService {
  /*  @Autowired
    private ContaDAO contaDAO;

    // Taxas para Conta Corrente
    private static final BigDecimal TAXA_COMUM = new BigDecimal("12.00");
    private static final BigDecimal TAXA_SUPER = new BigDecimal("8.00");

    // Rendimentos para Conta Poupança
    private static final BigDecimal RENDIMENTO_COMUM = new BigDecimal("0.005");
    private static final BigDecimal RENDIMENTO_SUPER = new BigDecimal("0.007");
    private static final BigDecimal RENDIMENTO_PREMIUM = new BigDecimal("0.009");

    @Scheduled(cron = "0 0 5 1 * ?") // Dia 1 de cada mês, às 05:00
    @Transactional
    public void aplicarTaxaManutencaoMensal() {
        List<Conta> contasCorrente = contaDAO.buscarTodasContasCorrenteAtivas();
        for (Conta conta : contasCorrente) {
            BigDecimal taxa = calcularTaxa(conta.getCliente().getCategoria());
            if (taxa.compareTo(BigDecimal.ZERO) > 0) {
                conta.setSaldo(conta.getSaldo().subtract(taxa));
                contaDAO.atualizar(conta);
            }
        }
    }

    @Scheduled(cron = "0 50 23 L * ?") // Último dia do mês, às 23:50
    @Transactional
    public void creditarRendimentoMensal() {
        List<Conta> contasPoupanca = contaDAO.buscarTodasContasPoupancaAtivas();
        for (Conta conta : contasPoupanca) {
            BigDecimal rendimento = calcularRendimento(conta);
            if (rendimento.compareTo(BigDecimal.ZERO) > 0) {
                conta.setSaldo(conta.getSaldo().add(rendimento));
        //        conta.setDataUltimoRendimento(LocalDate.now());
                contaDAO.atualizar(conta);
            }
        }
    }

    private BigDecimal calcularTaxa(CategoriaCliente tipoCliente) {
        return switch (tipoCliente) {
            case COMUM -> TAXA_COMUM;
            case SUPER -> TAXA_SUPER;
            case PREMIUM -> BigDecimal.ZERO;
        };
    }

    private BigDecimal calcularRendimento(Conta conta) {
        BigDecimal taxaAnual = switch (conta.getCliente().getCategoria()) {
            case COMUM -> RENDIMENTO_COMUM;
            case SUPER -> RENDIMENTO_SUPER;
            case PREMIUM -> RENDIMENTO_PREMIUM;
        };
        BigDecimal taxaMensal = taxaAnual.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        return conta.getSaldo().multiply(taxaMensal).setScale(2, RoundingMode.HALF_DOWN);
    } */
}
