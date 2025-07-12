package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RegraNegocioException;
import com.agsilvamhm.bancodigital.model.*;
import com.agsilvamhm.bancodigital.model.dto.*;
import com.agsilvamhm.bancodigital.repository.CartaoDao;
import com.agsilvamhm.bancodigital.repository.ContaDao;
import com.agsilvamhm.bancodigital.repository.MovimentacaoDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita o Mockito para JUnit 5
public class CartaoServiceTest {

    @Mock
    private CartaoDao cartaoDao;
    @Mock
    private ContaDao contaDao;
    @Mock
    private MovimentacaoDao movimentacaoDao;

    @InjectMocks // Injeta os mocks nas dependências do CartaoService
    private CartaoService cartaoService;

    private Cliente clienteSuper;
    private ContaCorrente contaCorrenteSuper;
    private Cartao cartaoCreditoSuper;
    private Cartao cartaoDebitoComum;

    @BeforeEach
    void setUp() {
        // Inicializa dados de teste comuns antes de cada teste
        clienteSuper = new Cliente();
        clienteSuper.setId(1);
        clienteSuper.setNome("Teste Super");
        clienteSuper.setCategoria(CategoriaCliente.SUPER); // Categoria SUPER para limite 5000

        contaCorrenteSuper = new ContaCorrente();
        contaCorrenteSuper.setId(100L);
        contaCorrenteSuper.setNumero("12345-6");
        contaCorrenteSuper.setAgencia("0001");
        contaCorrenteSuper.setSaldo(new BigDecimal("10000.00"));
        contaCorrenteSuper.setCliente(clienteSuper); // Associa o cliente à conta

        cartaoCreditoSuper = new Cartao();
        cartaoCreditoSuper.setId(1);
        cartaoCreditoSuper.setNumero("5432109876543210");
        cartaoCreditoSuper.setNomeTitular(clienteSuper.getNome());
        cartaoCreditoSuper.setTipoCartao(TipoCartao.CREDITO);
        cartaoCreditoSuper.setLimiteCredito(new BigDecimal("5000.00")); // Limite padrão SUPER
        cartaoCreditoSuper.setAtivo(true);
        cartaoCreditoSuper.setSenha("1234");
        cartaoCreditoSuper.setConta(contaCorrenteSuper);

        cartaoDebitoComum = new Cartao();
        cartaoDebitoComum.setId(2);
        cartaoDebitoComum.setNumero("4000111122223333");
        cartaoDebitoComum.setNomeTitular("Teste Comum");
        cartaoDebitoComum.setTipoCartao(TipoCartao.DEBITO);
        cartaoDebitoComum.setLimiteDiarioDebito(new BigDecimal("1000.00"));
        cartaoDebitoComum.setAtivo(true);
        cartaoDebitoComum.setSenha("4321");
        // Cria um cliente comum para o cartão de débito
        Cliente clienteComum = new Cliente();
        clienteComum.setId(2);
        clienteComum.setNome("Teste Comum");
        clienteComum.setCategoria(CategoriaCliente.COMUM);
        ContaCorrente contaCorrenteComum = new ContaCorrente();
        contaCorrenteComum.setId(200L);
        contaCorrenteComum.setNumero("98765-4");
        contaCorrenteComum.setAgencia("0001");
        contaCorrenteComum.setSaldo(new BigDecimal("2000.00"));
        contaCorrenteComum.setCliente(clienteComum);
        cartaoDebitoComum.setConta(contaCorrenteComum);
    }

    // --- Testes de Realizar Pagamento ---
    @Test
    @DisplayName("Deve realizar pagamento no crédito com sucesso")
    void deveRealizarPagamentoCreditoComSucesso() {
        // Cenário
        PagamentoCartaoRequest request = new PagamentoCartaoRequest(new BigDecimal("100.00"), "1234", "Compra Online");

        // Mocks
        when(cartaoDao.buscarPorId(cartaoCreditoSuper.getId())).thenReturn(Optional.of(cartaoCreditoSuper));
        doNothing().when(movimentacaoDao).salvar(any(Movimentacao.class)); // Simula salvar movimentação

        // Ação
        assertDoesNotThrow(() -> cartaoService.realizarPagamento(cartaoCreditoSuper.getId(), request));

        // Verificações
        verify(movimentacaoDao, times(1)).salvar(any(Movimentacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao pagar no crédito com limite insuficiente")
    void deveLancarExcecaoAoPagarCreditoComLimiteInsuficiente() {
        // Cenário
        PagamentoCartaoRequest request = new PagamentoCartaoRequest(new BigDecimal("6000.00"), "1234", "Compra Grande");

        // Mocks
        when(cartaoDao.buscarPorId(cartaoCreditoSuper.getId())).thenReturn(Optional.of(cartaoCreditoSuper));

        // Ação e Verificação
        assertThrows(RegraNegocioException.class, () ->
                cartaoService.realizarPagamento(cartaoCreditoSuper.getId(), request));
        verify(movimentacaoDao, never()).salvar(any(Movimentacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao pagar no débito com saldo insuficiente")
    void deveLancarExcecaoAoPagarDebitoComSaldoInsuficiente() {
        // Cenário
        PagamentoCartaoRequest request = new PagamentoCartaoRequest(new BigDecimal("3000.00"), "4321", "Compra Cara");

        // Mocks
        when(cartaoDao.buscarPorId(cartaoDebitoComum.getId())).thenReturn(Optional.of(cartaoDebitoComum));
        when(contaDao.buscarPorId(cartaoDebitoComum.getConta().getId())).thenReturn(Optional.of(cartaoDebitoComum.getConta()));

        // Ação e Verificação
        assertThrows(RegraNegocioException.class, () ->
                cartaoService.realizarPagamento(cartaoDebitoComum.getId(), request));
        verify(contaDao, never()).atualizar(any()); // Não deve atualizar a conta
        verify(movimentacaoDao, never()).salvar(any(Movimentacao.class));
    }

    // --- Testes de Alterar Status ---
    @Test
    @DisplayName("Deve alterar o status do cartão para inativo com sucesso")
    void deveAlterarStatusParaInativoComSucesso() {
        // Mocks
        when(cartaoDao.buscarPorId(cartaoCreditoSuper.getId())).thenReturn(Optional.of(cartaoCreditoSuper));
        when(cartaoDao.atualizarStatus(cartaoCreditoSuper.getId(), false)).thenReturn(1);

        // Ação
        assertDoesNotThrow(() -> cartaoService.alterarStatus(cartaoCreditoSuper.getId(), false));

        // Verificação
        verify(cartaoDao, times(1)).atualizarStatus(cartaoCreditoSuper.getId(), false);
    }

    // --- Testes de Atualizar Limite de Crédito ---
    @Test
    @DisplayName("Deve atualizar o limite de crédito com sucesso")
    void deveAtualizarLimiteCreditoComSucesso() {
        // Cenário
        BigDecimal novoLimite = new BigDecimal("7500.00");
        AtualizarLimiteRequest request = new AtualizarLimiteRequest(novoLimite);

        // Mocks
        when(cartaoDao.buscarPorId(cartaoCreditoSuper.getId())).thenReturn(Optional.of(cartaoCreditoSuper));
        when(cartaoDao.atualizarLimiteCredito(cartaoCreditoSuper.getId(), novoLimite)).thenReturn(1);

        // Ação
        assertDoesNotThrow(() -> cartaoService.atualizarLimiteCredito(cartaoCreditoSuper.getId(), request.novoLimite()));

        // Verificação
        verify(cartaoDao, times(1)).atualizarLimiteCredito(cartaoCreditoSuper.getId(), novoLimite);
    }

    @Test
    @DisplayName("Não deve atualizar limite de crédito para cartão de débito")
    void naoDeveAtualizarLimiteCreditoParaCartaoDebito() {
        // Cenário
        BigDecimal novoLimite = new BigDecimal("7500.00");
        AtualizarLimiteRequest request = new AtualizarLimiteRequest(novoLimite);

        // Mocks
        when(cartaoDao.buscarPorId(cartaoDebitoComum.getId())).thenReturn(Optional.of(cartaoDebitoComum));

        // Ação e Verificação
        assertThrows(RegraNegocioException.class, () ->
                cartaoService.atualizarLimiteCredito(cartaoDebitoComum.getId(), request.novoLimite()));
        verify(cartaoDao, never()).atualizarLimiteCredito(any(), any());
    }

    // --- Testes de Alterar Senha ---
    @Test
    @DisplayName("Deve alterar a senha do cartão com sucesso")
    void deveAlterarSenhaComSucesso() {
        // Cenário
        AtualizarSenhaRequestCartao request = new AtualizarSenhaRequestCartao("novaSenha123");

        // Mocks
        when(cartaoDao.buscarPorId(cartaoCreditoSuper.getId())).thenReturn(Optional.of(cartaoCreditoSuper));
        when(cartaoDao.atualizarSenha(cartaoCreditoSuper.getId(), "novaSenha123")).thenReturn(1); // Simula o hash

        // Ação
        assertDoesNotThrow(() -> cartaoService.alterarSenha(cartaoCreditoSuper.getId(), request.novaSenha()));

        // Verificação
        verify(cartaoDao, times(1)).atualizarSenha(cartaoCreditoSuper.getId(), "novaSenha123");
    }

      // --- Testes de Pagar Fatura ---
    @Test
    @DisplayName("Deve pagar fatura com sucesso e atualizar saldo da conta")
    void devePagarFaturaComSucesso() {
        // Cenário
        BigDecimal valorPagamento = new BigDecimal("500.00");
        PagarFaturaRequest request = new PagarFaturaRequest(valorPagamento);

        // Mocks
        when(cartaoDao.buscarPorId(cartaoCreditoSuper.getId())).thenReturn(Optional.of(cartaoCreditoSuper));
        doNothing().when(contaDao).atualizar(any()); // Simula atualização da conta
        doNothing().when(movimentacaoDao).salvar(any(Movimentacao.class));

        // Ação
        assertDoesNotThrow(() -> cartaoService.pagarFatura(cartaoCreditoSuper.getId(), request.valorPagamento()));

        // Verificações
        verify(contaDao, times(1)).atualizar(any());
        verify(movimentacaoDao, times(1)).salvar(any(Movimentacao.class));
        // Verifica que o saldo da conta foi debitado (10000 - 500 = 9500)
        assertEquals(new BigDecimal("9500.00"), contaCorrenteSuper.getSaldo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao pagar fatura de cartão de débito")
    void deveLancarExcecaoAoPagarFaturaDeCartaoDebito() {
        // Cenário
        BigDecimal valorPagamento = new BigDecimal("100.00");
        PagarFaturaRequest request = new PagarFaturaRequest(valorPagamento);

        // Mocks
        when(cartaoDao.buscarPorId(cartaoDebitoComum.getId())).thenReturn(Optional.of(cartaoDebitoComum));

        // Ação e Verificação
        assertThrows(RegraNegocioException.class, () ->
                cartaoService.pagarFatura(cartaoDebitoComum.getId(), request.valorPagamento()));
        verify(contaDao, never()).atualizar(any());
        verify(movimentacaoDao, never()).salvar(any(Movimentacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao pagar fatura com saldo insuficiente na conta")
    void deveLancarExcecaoAoPagarFaturaComSaldoInsuficiente() {
        // Cenário
        BigDecimal valorPagamento = new BigDecimal("15000.00"); // Mais que o saldo da conta
        PagarFaturaRequest request = new PagarFaturaRequest(valorPagamento);

        // Mocks
        when(cartaoDao.buscarPorId(cartaoCreditoSuper.getId())).thenReturn(Optional.of(cartaoCreditoSuper));

        // Ação e Verificação
        assertThrows(RegraNegocioException.class, () ->
                cartaoService.pagarFatura(cartaoCreditoSuper.getId(), request.valorPagamento()));
        verify(contaDao, never()).atualizar(any());
        verify(movimentacaoDao, never()).salvar(any(Movimentacao.class));
    }


    // --- Testes de Atualizar Limite Diário de Débito ---
    @Test
    @DisplayName("Deve atualizar o limite diário de débito com sucesso")
    void deveAtualizarLimiteDiarioDebitoComSucesso() {
        // Cenário
        BigDecimal novoLimiteDiario = new BigDecimal("1500.00");
        AtualizarLimiteDiarioRequest request = new AtualizarLimiteDiarioRequest(novoLimiteDiario);

        // Mocks
        when(cartaoDao.buscarPorId(cartaoDebitoComum.getId())).thenReturn(Optional.of(cartaoDebitoComum));
        when(cartaoDao.atualizarLimiteDiarioDebito(cartaoDebitoComum.getId(), novoLimiteDiario)).thenReturn(1);

        // Ação
        assertDoesNotThrow(() -> cartaoService.atualizarLimiteDiarioDebito(cartaoDebitoComum.getId(), request.novoLimiteDiario()));

        // Verificação
        verify(cartaoDao, times(1)).atualizarLimiteDiarioDebito(cartaoDebitoComum.getId(), novoLimiteDiario);
    }

    @Test
    @DisplayName("Não deve atualizar limite diário de débito para cartão de crédito")
    void naoDeveAtualizarLimiteDiarioDebitoParaCartaoCredito() {
        // Cenário
        BigDecimal novoLimiteDiario = new BigDecimal("1500.00");
        AtualizarLimiteDiarioRequest request = new AtualizarLimiteDiarioRequest(novoLimiteDiario);

        // Mocks
        when(cartaoDao.buscarPorId(cartaoCreditoSuper.getId())).thenReturn(Optional.of(cartaoCreditoSuper));

        // Ação e Verificação
        assertThrows(RegraNegocioException.class, () ->
                cartaoService.atualizarLimiteDiarioDebito(cartaoCreditoSuper.getId(), request.novoLimiteDiario()));
        verify(cartaoDao, never()).atualizarLimiteDiarioDebito(any(), any());
    }

    // --- Testes de Validação Comuns (exemplos) ---
    @Test
    @DisplayName("Deve lançar exceção ao buscar cartão inexistente")
    void deveLancarExcecaoAoBuscarCartaoInexistente() {
        // Mocks
        when(cartaoDao.buscarPorId(999)).thenReturn(Optional.empty());

        // Ação e Verificação
        assertThrows(EntidadeNaoEncontradaException.class, () ->
                cartaoService.buscarPorId(999));
    }

    @Test
    @DisplayName("Deve lançar exceção ao realizar pagamento com cartão inativo")
    void deveLancarExcecaoAoRealizarPagamentoComCartaoInativo() {
        // Cenário
        cartaoCreditoSuper.setAtivo(false); // Inativa o cartão para o teste
        PagamentoCartaoRequest request = new PagamentoCartaoRequest(new BigDecimal("100.00"), "1234", "Compra");

        // Mocks
        when(cartaoDao.buscarPorId(cartaoCreditoSuper.getId())).thenReturn(Optional.of(cartaoCreditoSuper));

        // Ação e Verificação
        assertThrows(RegraNegocioException.class, () ->
                cartaoService.realizarPagamento(cartaoCreditoSuper.getId(), request));
    }

    @Test
    @DisplayName("Deve lançar exceção ao realizar pagamento com senha incorreta")
    void deveLancarExcecaoAoRealizarPagamentoComSenhaIncorreta() {
        // Cenário
        PagamentoCartaoRequest request = new PagamentoCartaoRequest(new BigDecimal("100.00"), "senhaErrada", "Compra");

        // Mocks
        when(cartaoDao.buscarPorId(cartaoCreditoSuper.getId())).thenReturn(Optional.of(cartaoCreditoSuper));

        // Ação e Verificação
        assertThrows(RegraNegocioException.class, () ->
                cartaoService.realizarPagamento(cartaoCreditoSuper.getId(), request));
    }
}