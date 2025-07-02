package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.dao.ContaDao;
import com.agsilvamhm.bancodigital.entity.Conta;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ContaService {

    private static final Logger logger = LoggerFactory.getLogger(ContaService.class);

    private final ContaDao contaDao;

    @Autowired
    public ContaService(ContaDao contaDao) {
        this.contaDao = contaDao;
    }

    @Transactional
    public Conta criarConta(Conta conta) {
        Objects.requireNonNull(conta, "O objeto conta não pode ser nulo.");
        validarConta(conta);

        try {
            Integer idNovaConta = contaDao.salvar(conta);
            logger.info("Serviço: Conta ID {} para o cliente ID {} foi criada com sucesso.", idNovaConta, conta.getCliente().getId());

             return buscarPorId(idNovaConta);

        } catch (RepositorioException ex) {
            logger.error("Serviço: Erro ao tentar criar conta: {}", ex.getMessage());
            throw ex;
        }
    }

    public Conta buscarPorId(Integer id) {
        Objects.requireNonNull(id, "O ID da conta não pode ser nulo.");
        return contaDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta com ID " + id + " não encontrada."));
    }

    public List<Conta> buscarContasPorCliente(Integer idCliente) {
        Objects.requireNonNull(idCliente, "O ID do cliente não pode ser nulo.");
        return contaDao.buscarPorCliente(idCliente);
    }

    private void validarConta(Conta conta) {
        if (conta.getNumero() == null || conta.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("O número da conta não pode ser vazio.");
        }
        if (conta.getAgencia() == null || conta.getAgencia().trim().isEmpty()) {
            throw new IllegalArgumentException("A agência da conta não pode ser vazia.");
        }
      //  if (conta.getCliente().getId() == null) {
      //      throw new IllegalArgumentException("A conta deve estar associada a um cliente com ID válido.");
     //   }
    }
}