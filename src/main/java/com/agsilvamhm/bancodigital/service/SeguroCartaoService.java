package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.Repository.CartaoDao;
import com.agsilvamhm.bancodigital.Repository.SeguroCartaoDao;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RegraNegocioException;
import com.agsilvamhm.bancodigital.model.Cartao;
import com.agsilvamhm.bancodigital.model.SeguroCartao;
import com.agsilvamhm.bancodigital.model.TipoCartao;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class SeguroCartaoService {

    private static final Logger logger = LoggerFactory.getLogger(SeguroCartaoService.class);

    private final SeguroCartaoDao seguroCartaoDao;
    private final CartaoDao cartaoDao;

    @Autowired
    public SeguroCartaoService(SeguroCartaoDao seguroCartaoDao, CartaoDao cartaoDao) {
        this.seguroCartaoDao = seguroCartaoDao;
        this.cartaoDao = cartaoDao;
    }

    @Transactional
    public SeguroCartao contratarSeguro(Integer cartaoId, BigDecimal valorPremio, String cobertura, String condicoes) {
        Cartao cartao = cartaoDao.buscarPorId(cartaoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cartão com ID " + cartaoId + " não encontrado."));

        if (TipoCartao.DEBITO.equals(cartao.getTipoCartao())) {
            throw new RegraNegocioException("Seguros estão disponíveis apenas para cartões de crédito.");
        }

        if (seguroCartaoDao.buscarPorCartaoId(cartaoId).isPresent()) {
            throw new RegraNegocioException("Este cartão já possui um seguro ativo.");
        }

        SeguroCartao novoSeguro = new SeguroCartao();
        novoSeguro.setCartao(cartao);
        novoSeguro.setNumeroApolice(UUID.randomUUID().toString());
        novoSeguro.setDataContratacao(java.time.LocalDateTime.now());
        novoSeguro.setCobertura(cobertura);
        novoSeguro.setCondicoes(condicoes);
        novoSeguro.setValorPremio(valorPremio);

        SeguroCartao seguroSalvo = seguroCartaoDao.salvar(novoSeguro);
        logger.info("Seguro contratado com sucesso para o cartão ID {}. Apólice: {}", cartaoId, seguroSalvo.getNumeroApolice());
        return seguroSalvo;
    }

    public SeguroCartao buscarPorId(Integer seguroId) {
        return seguroCartaoDao.buscarPorId(seguroId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Seguro com ID " + seguroId + " não encontrado."));
    }

    public List<SeguroCartao> listarTodos() {
        return seguroCartaoDao.listarTodos();
    }

    @Transactional
    public void cancelarSeguro(Integer seguroId) {
        // Garante que o seguro existe antes de tentar cancelar
        buscarPorId(seguroId);
        int rowsAffected = seguroCartaoDao.cancelar(seguroId);
        if (rowsAffected > 0) {
            logger.info("Seguro ID {} cancelado com sucesso.", seguroId);
        } else {
            // Esta exceção não deveria ser alcançada por causa da verificação acima, mas é uma boa prática.
            throw new EntidadeNaoEncontradaException("Seguro com ID " + seguroId + " não encontrado para cancelamento.");
        }
    }
}

