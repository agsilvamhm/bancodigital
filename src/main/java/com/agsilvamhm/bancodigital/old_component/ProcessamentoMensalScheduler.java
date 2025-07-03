package com.agsilvamhm.bancodigital.old_component;

import org.springframework.stereotype.Component;

@Component
public class ProcessamentoMensalScheduler {
/*
    private static final Logger logger = LoggerFactory.getLogger(ProcessamentoMensalScheduler.class);

    private final ContaDao contaDao;
    private final OperacoesContaService operacoesContaService;

    @Autowired
    public ProcessamentoMensalScheduler(ContaDao contaDao, OperacoesContaService operacoesContaService) {
        this.contaDao = contaDao;
        this.operacoesContaService = operacoesContaService;
    }


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
    } */
}
