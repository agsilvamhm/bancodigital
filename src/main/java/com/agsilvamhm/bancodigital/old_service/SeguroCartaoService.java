package com.agsilvamhm.bancodigital.old_service;

import com.agsilvamhm.bancodigital.old_dao.SeguroCartaoDao;
import com.agsilvamhm.bancodigital.old_entity.SeguroCartao;
import org.springframework.stereotype.Service;

@Service
public class SeguroCartaoService {
    private final SeguroCartaoDao seguroCartaoDao;
    private final CartaoService cartaoService;

    public SeguroCartaoService(SeguroCartaoDao seguroCartaoDao, CartaoService cartaoService) {
        this.seguroCartaoDao = seguroCartaoDao;
        this.cartaoService = cartaoService;
    }

    public SeguroCartao contratarSeguro(Integer idCartaoCredito) { /* Lógica para criar e salvar seguro */ return new SeguroCartao(); }
    public SeguroCartao buscarSeguroPorCartao(Integer idCartao) { /* Lógica para buscar seguro */ return new SeguroCartao(); }
}