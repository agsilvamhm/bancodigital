package com.agsilvamhm.bancodigital.old_service;

import org.springframework.stereotype.Service;

@Service
public class OperacoesContaService {
/*
    private static final Logger logger = LoggerFactory.getLogger(OperacoesContaService.class);
    private final ContaDao contaDao;

    @Autowired
    public OperacoesContaService(ContaDao contaDao) {
        this.contaDao = contaDao;
    }


    @Transactional
    public void aplicarTaxaManutencao(ContaCorrente conta) {
        double taxa = 0.0;
        CategoriaCliente categoria = conta.getCliente().getCategoria();

        switch (categoria) {
            case COMUM:   taxa = 12.00; break;
            case SUPER:   taxa = 8.00;  break;
            case PREMIUM: taxa = 0.0;   break; // Isento
        }

        if (taxa > 0) {
            conta.debitar(taxa);
            contaDao.atualizarSaldo(conta.getId(), conta.getSaldo());
            logger.info("Taxa de R$ {} aplicada na conta corrente ID {}.", String.format("%.2f", taxa), conta.getId());
        }
    }


    @Transactional
    public void aplicarRendimento(ContaPoupanca conta) {
        double taxaAnual = 0.0;
        CategoriaCliente categoria = conta.getCliente().getCategoria();

        switch (categoria) {
            case COMUM:   taxaAnual = 0.005; break; // 0.5%
            case SUPER:   taxaAnual = 0.007; break; // 0.7%
            case PREMIUM: taxaAnual = 0.009; break; // 0.9%
        }

        // Fórmula para taxa mensal (im) a partir da anual (ia): im = (1 + ia)^(1/12) - 1
        double taxaMensal = Math.pow(1 + taxaAnual, 1.0 / 12.0) - 1;
        double rendimento = conta.getSaldo() * taxaMensal;

        if (rendimento > 0) {
            conta.creditar(rendimento);
            contaDao.atualizarSaldo(conta.getId(), conta.getSaldo());
            logger.info("Rendimento de R$ {} aplicado na conta poupança ID {}.", String.format("%.2f", rendimento), conta.getId());
        }
    } */
}