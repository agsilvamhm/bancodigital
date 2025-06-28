package com.agsilvamhm.bancodigital.component;

import com.agsilvamhm.bancodigital.dao.ContaDao;
import com.agsilvamhm.bancodigital.entity.ContaCorrente;
import com.agsilvamhm.bancodigital.entity.ContaPoupanca;
import com.agsilvamhm.bancodigital.service.OperacoesContaService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcessamentoMensalScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ProcessamentoMensalScheduler.class);

    private final ContaDao contaDao;
    private final OperacoesContaService operacoesContaService;

    @Autowired
    public ProcessamentoMensalScheduler(ContaDao contaDao, OperacoesContaService operacoesContaService) {
        this.contaDao = contaDao;
        this.operacoesContaService = operacoesContaService;
    }

    /**
     * Executa todo dia 1 de cada mês, à 01:00 da manhã.
     * Busca todas as contas correntes e aplica a taxa de manutenção.
     * Cron: (segundo minuto hora dia-do-mês mês dia-da-semana)
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void processarTaxasDeManutencao() {
        logger.info("INICIANDO processo agendado de aplicação de taxas de manutenção.");
        List<ContaCorrente> contas = contaDao.buscarTodasContasCorrentes();
        logger.info("Encontradas {} contas correntes para processamento.", contas.size());
        for (ContaCorrente conta : contas) {
            try {
                operacoesContaService.aplicarTaxaManutencao(conta);
            } catch (Exception e) {
                // Loga o erro mas continua o processo para as outras contas
                logger.error("Falha ao processar taxa para a conta ID {}: {}", conta.getId(), e.getMessage());
            }
        }
        logger.info("FINALIZADO processo de taxas de manutenção.");
    }

    /**
     * Executa todo dia 1 de cada mês, às 01:05 da manhã.
     */
    @Scheduled(cron = "0 5 1 * * ?")
    @Transactional
    public void processarRendimentosPoupanca() {
        logger.info("INICIANDO processo agendado de aplicação de rendimentos.");
        List<ContaPoupanca> contas = contaDao.buscarTodasContasPoupanca();
        logger.info("Encontradas {} contas poupança para processamento.", contas.size());
        for (ContaPoupanca conta : contas) {
            try {
                operacoesContaService.aplicarRendimento(conta);
            } catch (Exception e) {
                logger.error("Falha ao processar rendimento para a conta ID {}: {}", conta.getId(), e.getMessage());
            }
        }
        logger.info("FINALIZADO processo de rendimentos.");
    }
}
