package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RegraNegocioException;
import com.agsilvamhm.bancodigital.model.*;
import com.agsilvamhm.bancodigital.model.dto.CriarContaRequest;
import com.agsilvamhm.bancodigital.repository.ClienteDao;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaDao contaDao;
    @Mock
    private ClienteDao clienteDao;
    @Mock
    private MovimentacaoDao movimentacaoDao;

    @InjectMocks
    private ContaService contaService;

    private Cliente clienteComum;
    private Cliente clientePremium;
    private ContaCorrente contaCorrente;
    private ContaPoupanca contaPoupanca;

    @BeforeEach
    void setUp() {
        clienteComum = new Cliente();
        clienteComum.setId(1);
        clienteComum.setNome("Cliente Comum");
        clienteComum.setCategoria(CategoriaCliente.COMUM);
        clienteComum.setDataNascimento(LocalDate.of(1990, 1, 1));

        clientePremium = new Cliente();
        clientePremium.setId(2);
        clientePremium.setNome("Cliente Premium");
        clientePremium.setCategoria(CategoriaCliente.PREMIUM);

        contaCorrente = new ContaCorrente();
        contaCorrente.setId(10L);
        contaCorrente.setNumero("12345-6");
        contaCorrente.setAgencia("0001");
        contaCorrente.setCliente(clienteComum);
        contaCorrente.setSaldo(new BigDecimal("1000.00"));

        contaPoupanca = new ContaPoupanca();
        contaPoupanca.setId(20L);
        contaPoupanca.setNumero("54321-0");
        contaPoupanca.setAgencia("0001");
        contaPoupanca.setCliente(clienteComum);
        contaPoupanca.setSaldo(new BigDecimal("5000.00"));
    }

    @Test
    @DisplayName("Deve criar uma conta corrente com sucesso")
    void criarConta_deveCriarContaCorrente_quandoDadosValidos() {
        var request = new CriarContaRequest(clienteComum.getId(), "11122-3", "0001", TipoConta.CORRENTE);

        when(clienteDao.buscarPorId(clienteComum.getId())).thenReturn(Optional.of(clienteComum));
        when(contaDao.buscarPorNumero(anyString())).thenReturn(Optional.empty());

        when(contaDao.salvar(any(ContaCorrente.class))).thenAnswer(invocation -> {
            ContaCorrente contaSalva = invocation.getArgument(0);
            contaSalva.setId(1L); // Simula a geração de um ID pelo banco
            return contaSalva;
        });

        Conta novaConta = contaService.criarConta(request);
        assertNotNull(novaConta);
        assertTrue(novaConta instanceof ContaCorrente);
        assertEquals(request.numero(), novaConta.getNumero());
        assertEquals(clienteComum.getId(), novaConta.getCliente().getId());
        assertEquals(BigDecimal.ZERO, novaConta.getSaldo());
        verify(contaDao, times(1)).salvar(any(ContaCorrente.class));
    }

    @Test
    @DisplayName("Não deve criar conta se o cliente não existir")
    void criarConta_deveLancarExcecao_quandoClienteNaoEncontrado() {
        var request = new CriarContaRequest(999, "11122-3", "0001", TipoConta.CORRENTE);
        when(clienteDao.buscarPorId(999)).thenReturn(Optional.empty());
        assertThrows(EntidadeNaoEncontradaException.class, () -> {
            contaService.criarConta(request);
        });
        verify(contaDao, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve aplicar taxa de manutenção para cliente Comum")
    void aplicarTaxaManutencao_deveDebitarValor_paraClienteComum() {
        when(contaDao.buscarPorId(contaCorrente.getId())).thenReturn(Optional.of(contaCorrente));
        BigDecimal saldoInicial = contaCorrente.getSaldo();
        BigDecimal taxaEsperada = CategoriaCliente.COMUM.getTaxaManutencao();
        Movimentacao movimentacao = contaService.aplicarTaxaManutencao(contaCorrente.getId());
        assertNotNull(movimentacao);
        assertEquals(saldoInicial.subtract(taxaEsperada), contaCorrente.getSaldo());
        assertEquals(taxaEsperada.doubleValue(), movimentacao.getValor());
        verify(contaDao, times(1)).atualizar(contaCorrente);
        verify(movimentacaoDao, times(1)).salvar(any(Movimentacao.class));
    }

    @Test
    @DisplayName("Não deve aplicar taxa de manutenção para cliente Premium")
    void aplicarTaxaManutencao_naoDeveFazerNada_paraClientePremium() {
        contaCorrente.setCliente(clientePremium); // Troca o cliente da conta para Premium
        when(contaDao.buscarPorId(contaCorrente.getId())).thenReturn(Optional.of(contaCorrente));
        Movimentacao movimentacao = contaService.aplicarTaxaManutencao(contaCorrente.getId());
        assertNull(movimentacao);
        verify(contaDao, never()).atualizar(any());
        verify(movimentacaoDao, never()).salvar(any());
    }

    @Test
    @DisplayName("Não deve aplicar taxa de manutenção se saldo for insuficiente")
    void aplicarTaxaManutencao_deveLancarExcecao_quandoSaldoInsuficiente() {
        contaCorrente.setSaldo(new BigDecimal("5.00")); // Saldo menor que a taxa de R$12.00
        when(contaDao.buscarPorId(contaCorrente.getId())).thenReturn(Optional.of(contaCorrente));
        assertThrows(RegraNegocioException.class, () -> {
            contaService.aplicarTaxaManutencao(contaCorrente.getId());
        });
        verify(contaDao, never()).atualizar(any());
        verify(movimentacaoDao, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve aplicar rendimentos em conta poupança")
    void aplicarRendimentos_deveCreditarValor_paraContaPoupanca() {
        when(contaDao.buscarPorId(contaPoupanca.getId())).thenReturn(Optional.of(contaPoupanca));
        BigDecimal saldoInicial = contaPoupanca.getSaldo();
        BigDecimal taxaMensal = CategoriaCliente.COMUM.getTaxaRendimentoMensalEquivalente();
        BigDecimal rendimentoEsperado = saldoInicial.multiply(taxaMensal);
        contaService.aplicarRendimentos(contaPoupanca.getId());
        assertThat(contaPoupanca.getSaldo().toString()).startsWith(saldoInicial.add(rendimentoEsperado).toString().substring(0, 4));
        verify(contaDao, times(1)).atualizar(contaPoupanca);
        verify(movimentacaoDao, times(1)).salvar(any(Movimentacao.class));
    }

    @Test
    @DisplayName("Não deve aplicar rendimentos se o tipo da conta for incorreto")
    void aplicarRendimentos_deveLancarExcecao_quandoContaNaoForPoupanca() {
        when(contaDao.buscarPorId(contaCorrente.getId())).thenReturn(Optional.of(contaCorrente));
        Exception exception = assertThrows(RegraNegocioException.class, () -> {
            contaService.aplicarRendimentos(contaCorrente.getId());
        });
        assertEquals("Rendimentos só podem ser aplicados a Contas Poupança.", exception.getMessage());
        verify(contaDao, never()).atualizar(any());
    }
}
