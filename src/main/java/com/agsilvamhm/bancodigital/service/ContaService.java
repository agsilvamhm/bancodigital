package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.Repository.ClienteDao;
import com.agsilvamhm.bancodigital.Repository.ContaDao;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RegraNegocioException;
import com.agsilvamhm.bancodigital.controller.exception.RepositorioException;
import com.agsilvamhm.bancodigital.model.Cliente;
import com.agsilvamhm.bancodigital.model.Conta;
import com.agsilvamhm.bancodigital.model.ContaCorrente;
import com.agsilvamhm.bancodigital.model.ContaPoupanca;
import com.agsilvamhm.bancodigital.model.dto.CriarContaRequest;
import com.agsilvamhm.bancodigital.old_entity.TipoConta;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class ContaService {

    // ... (Logger e Construtor permanecem os mesmos) ...
    private static final Logger logger = LoggerFactory.getLogger(ContaService.class);

    private final ContaDao contaDao;
    private final ClienteDao clienteDao;

    @Autowired
    public ContaService(ContaDao contaDao, ClienteDao clienteDao) {
        this.contaDao = contaDao;
        this.clienteDao = clienteDao;
    }

    @Transactional
    public Conta criarConta(CriarContaRequest request) {
        Objects.requireNonNull(request, "A requisição para criar conta não pode ser nula.");
        validarNovaConta(request);

        Cliente cliente = clienteDao.buscarPorId(request.clienteId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente com ID " + request.clienteId() + " não encontrado."));

        Conta novaConta;
        if (TipoConta.CORRENTE.equals(request.tipoConta())) {
            // AJUSTE AQUI: Validar que a taxa de manutenção não é nula para conta corrente.
            if (request.taxaManutencao() == null) {
                throw new IllegalArgumentException("A taxa de manutenção é obrigatória para Conta Corrente.");
            }
            ContaCorrente cc = new ContaCorrente();
            cc.setTaxaManutencaoMensal(request.taxaManutencao());
            novaConta = cc;

        } else if (TipoConta.POUPANCA.equals(request.tipoConta())) {
            // AJUSTE AQUI: Validar que a taxa de rendimento não é nula para conta poupança.
            if (request.taxaRendimento() == null) {
                throw new IllegalArgumentException("A taxa de rendimento é obrigatória para Conta Poupança.");
            }
            ContaPoupanca cp = new ContaPoupanca();
            cp.setTaxaRendimentoMensal(request.taxaRendimento());
            novaConta = cp;

        } else {
            throw new IllegalArgumentException("Tipo de conta inválido: " + request.tipoConta());
        }

        novaConta.setCliente(cliente);
        novaConta.setNumero(request.numero());
        novaConta.setAgencia(request.agencia());
        novaConta.setSaldo(BigDecimal.ZERO);

        try {
            Conta contaSalva = contaDao.salvar(novaConta);
            logger.info("Serviço: Conta ID {} para o cliente {} foi criada com sucesso.", contaSalva.getId(), cliente.getNome());
            return contaSalva;
        } catch (DataAccessException ex) {
            logger.error("Serviço: Erro de persistência ao tentar criar conta.", ex);
            throw new RepositorioException("Erro ao salvar a conta no banco de dados.", ex);
        }
    }

    // ... O resto da classe (buscarPorId, buscarPorNumero, listar, atualizar, validar) está correto ...
    public Conta buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID da conta não pode ser nulo.");
        return contaDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta com ID " + id + " não encontrada."));
    }

    public Conta buscarPorNumero(String numero) {
        Objects.requireNonNull(numero, "O número da conta não pode ser nulo.");
        return contaDao.buscarPorNumero(numero)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta com número " + numero + " não encontrada."));
    }

    public List<Conta> listarTodasContasCorrente() {
        return contaDao.listarContasCorrente();
    }

    public List<Conta> listarTodasContasPoupanca() {
        return contaDao.listarContasPoupanca();
    }

    @Transactional
    public void atualizarConta(Conta conta) {
        contaDao.atualizar(conta);
    }

    private void validarNovaConta(CriarContaRequest request) {
        if (request.numero() == null || request.numero().trim().isEmpty()) {
            throw new IllegalArgumentException("O número da conta não pode ser vazio.");
        }
        if (request.agencia() == null || request.agencia().trim().isEmpty()) {
            throw new IllegalArgumentException("A agência da conta não pode ser vazia.");
        }
        if (contaDao.buscarPorNumero(request.numero()).isPresent()) {
            throw new RegraNegocioException("Uma conta com o número '" + request.numero() + "' já existe.");
        }
    }
}