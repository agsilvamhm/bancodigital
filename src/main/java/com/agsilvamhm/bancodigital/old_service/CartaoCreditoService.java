package com.agsilvamhm.bancodigital.old_service;

import org.springframework.stereotype.Service;

@Service
public class CartaoCreditoService {
/*
    private final CartaoCreditoDao cartaoCreditoDao;
    private final TransacaoCartaoDao transacaoCartaoDao;
    private final ContaService contaService; // Reutilizando o serviço de conta

    @Autowired
    public CartaoCreditoService(CartaoCreditoDao cartaoCreditoDao, TransacaoCartaoDao transacaoCartaoDao, ContaService contaService) {
        this.cartaoCreditoDao = cartaoCreditoDao;
        this.transacaoCartaoDao = transacaoCartaoDao;
        this.contaService = contaService;
    }

    @Transactional
    public CartaoCredito criarCartao(Integer idConta) {
        Conta conta = contaService.buscarPorId(idConta);
        Cliente cliente = conta.getCliente();

        CartaoCredito cartao = new CartaoCredito();
        cartao.setConta(conta);

        // REGRA DE NEGÓCIO: Definir limite com base na categoria do cliente
        switch (cliente.getCategoria()) {
            case COMUM:
                cartao.setLimiteCredito(1000.00);
                break;
            case SUPER:
                cartao.setLimiteCredito(5000.00);
                break;
            case PREMIUM:
                cartao.setLimiteCredito(10000.00);
                // REGRA DE NEGÓCIO: Seguro Viagem gratuito para Premium
                cartao.setPossuiSeguroViagem(true);
                break;
        }

        // Gerar dados do cartão (número, validade, cvv)
        cartao.setNumero(UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 16));
        cartao.setValidade(LocalDate.now().plusYears(5));
        cartao.setCvv(UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 3));

        cartaoCreditoDao.salvar(cartao);
        return cartao;
    }

    @Transactional
    public void contratarSeguroViagem(Integer cartaoId) {
        CartaoCredito cartao = cartaoCreditoDao.buscarPorId(cartaoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cartão de crédito não encontrado."));

        Cliente cliente = cartao.getConta().getCliente();
        if (cliente.getCategoria() == CategoriaCliente.PREMIUM) {
            throw new RegraNegocioException("Clientes Premium já possuem Seguro Viagem incluído.");
        }

        if (cartao.isPossuiSeguroViagem()) {
            throw new RegraNegocioException("Seguro Viagem já está ativo para este cartão.");
        }

        cartaoCreditoDao.atualizarSeguroViagem(cartaoId, true);
    } */
}
