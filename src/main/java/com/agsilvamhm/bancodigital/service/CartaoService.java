package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.entity.Cartao;
import com.agsilvamhm.bancodigital.repository.CartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class CartaoService {
    @Autowired
    private CartaoRepository cartaoRepository;

    public Cartao ativarCartao(Long id) {
        Optional<Cartao> cartao = cartaoRepository.findById(id);
        cartao.ifPresent(c -> {
        //    c.setAtivo(true);
            cartaoRepository.save(c);
        });
        return cartao.orElse(null);
    }
}
