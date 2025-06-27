package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.dao.CartaoDao;
import com.agsilvamhm.bancodigital.entity.Cartao;
import com.agsilvamhm.bancodigital.entity.TipoCartao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartaoService {
    private final CartaoDao cartaoDao;

    public CartaoService(CartaoDao cartaoDao) { this.cartaoDao = cartaoDao; }

    public Cartao emitirCartao(Integer idConta, TipoCartao tipo) { /* Lógica para gerar e salvar cartão */ return new Cartao(); }
    public Cartao ativarDesativarCartao(Integer idCartao, boolean ativar) { /* Lógica para mudar status */ return new Cartao(); }
    public void alterarSenha(Integer idCartao, String novaSenha) { /* Lógica para alterar senha */ }
    public Cartao ajustarLimite(Integer idCartao, BigDecimal novoLimite) { /* Lógica para ajustar limite */ return new Cartao(); }
    public List<Cartao> buscarCartoesPorConta(Integer idConta) { return cartaoDao.buscarPorConta(idConta); }
}