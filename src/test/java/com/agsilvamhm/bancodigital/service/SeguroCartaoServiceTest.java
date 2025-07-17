package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.controller.exception.RegraNegocioException;
import com.agsilvamhm.bancodigital.model.Cartao;
import com.agsilvamhm.bancodigital.model.SeguroCartao;
import com.agsilvamhm.bancodigital.model.TipoCartao;
import com.agsilvamhm.bancodigital.repository.CartaoDao;
import com.agsilvamhm.bancodigital.repository.SeguroCartaoDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class) // Habilita o Mockito para JUnit 5
class SeguroCartaoServiceTest {

    @Mock // Cria um mock para CartaoDao
    private CartaoDao cartaoDao;

    @Mock // Cria um mock para SeguroCartaoDao
    private SeguroCartaoDao seguroCartaoDao;

    @InjectMocks // Injeta os mocks acima nesta instância de SeguroCartaoService
    private SeguroCartaoService seguroCartaoService;

    private Cartao cartaoCredito;
    private Cartao cartaoDebito;
    private SeguroCartao seguroExistente;

    @BeforeEach
    void setUp() {
        // Inicializa objetos de teste antes de cada método de teste
        cartaoCredito = new Cartao();
        cartaoCredito.setId(1);
        cartaoCredito.setNumero("1111-2222-3333-4444");
        cartaoCredito.setTipoCartao(TipoCartao.CREDITO);
        cartaoCredito.setNomeTitular("TESTE TITULAR");

        cartaoDebito = new Cartao();
        cartaoDebito.setId(2);
        cartaoDebito.setNumero("5555-6666-7777-8888");
        cartaoDebito.setTipoCartao(TipoCartao.DEBITO);
        cartaoDebito.setNomeTitular("TESTE TITULAR DEBITO");

        seguroExistente = new SeguroCartao();
        seguroExistente.setId(100);
        seguroExistente.setCartao(cartaoCredito);
        seguroExistente.setNumeroApolice("APOLICE-EXISTENTE-123");
        seguroExistente.setDataContratacao(LocalDateTime.now().minusDays(5));
        seguroExistente.setValorPremio(new BigDecimal("50.00"));
        seguroExistente.setCobertura("Perda e Roubo");
    }

    // --- Testes para contratarSeguro ---

    @Test
    @DisplayName("Deve contratar um seguro para cartão de crédito com sucesso")
    void deveContratarSeguroParaCartaoDeCreditoComSucesso() {
        // Cenário: Cartão de crédito existe e não tem seguro ativo
        when(cartaoDao.buscarPorId(1)).thenReturn(Optional.of(cartaoCredito));
        when(seguroCartaoDao.buscarPorCartaoId(1)).thenReturn(Optional.empty());
        // Simula o salvamento, capturando o argumento e definindo o ID
        when(seguroCartaoDao.salvar(any(SeguroCartao.class))).thenAnswer(invocation -> {
            SeguroCartao seguro = invocation.getArgument(0);
            seguro.setId(1); // Simula o ID gerado pelo banco
            return seguro;
        });

        BigDecimal valorPremio = new BigDecimal("25.00");
        String cobertura = "Roubo e Furto";
        String condicoes = "Condições gerais do seguro";

        SeguroCartao seguroContratado = seguroCartaoService.contratarSeguro(1, valorPremio, cobertura, condicoes);

        assertNotNull(seguroContratado);
        assertEquals(1, seguroContratado.getId());
        assertEquals(cartaoCredito, seguroContratado.getCartao());
        assertNotNull(seguroContratado.getNumeroApolice()); // Deve ser gerado um UUID
        assertEquals(valorPremio, seguroContratado.getValorPremio());
        assertEquals(cobertura, seguroContratado.getCobertura());
        assertEquals(condicoes, seguroContratado.getCondicoes());
        assertNotNull(seguroContratado.getDataContratacao());

        // Verifica se os métodos corretos foram chamados
        verify(cartaoDao, times(1)).buscarPorId(1);
        verify(seguroCartaoDao, times(1)).buscarPorCartaoId(1);
        verify(seguroCartaoDao, times(1)).salvar(any(SeguroCartao.class));
    }

    @Test
    @DisplayName("Deve lançar EntidadeNaoEncontradaException se o cartão não for encontrado")
    void deveLancarExcecaoSeCartaoNaoEncontrado() {
        when(cartaoDao.buscarPorId(999)).thenReturn(Optional.empty());

        BigDecimal valorPremio = new BigDecimal("25.00");
        String cobertura = "Roubo e Furto";
        String condicoes = "Condições gerais do seguro";

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () ->
                seguroCartaoService.contratarSeguro(999, valorPremio, cobertura, condicoes)
        );

        assertEquals("Cartão com ID 999 não encontrado.", exception.getMessage());
        verify(cartaoDao, times(1)).buscarPorId(999);
        verify(seguroCartaoDao, never()).buscarPorCartaoId(anyInt()); // Não deve chamar o DAO de seguro
        verify(seguroCartaoDao, never()).salvar(any(SeguroCartao.class)); // Não deve salvar
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException se o cartão for de débito")
    void deveLancarExcecaoSeCartaoForDebito() {
        when(cartaoDao.buscarPorId(2)).thenReturn(Optional.of(cartaoDebito));

        BigDecimal valorPremio = new BigDecimal("25.00");
        String cobertura = "Roubo e Furto";
        String condicoes = "Condições gerais do seguro";

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () ->
                seguroCartaoService.contratarSeguro(2, valorPremio, cobertura, condicoes)
        );

        assertEquals("Seguros estão disponíveis apenas para cartões de crédito.", exception.getMessage());
        verify(cartaoDao, times(1)).buscarPorId(2);
        verify(seguroCartaoDao, never()).buscarPorCartaoId(anyInt());
        verify(seguroCartaoDao, never()).salvar(any(SeguroCartao.class));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException se o cartão já possuir um seguro")
    void deveLancarExcecaoSeCartaoJaPossuirSeguro() {
        when(cartaoDao.buscarPorId(1)).thenReturn(Optional.of(cartaoCredito));
        when(seguroCartaoDao.buscarPorCartaoId(1)).thenReturn(Optional.of(seguroExistente));

        BigDecimal valorPremio = new BigDecimal("25.00");
        String cobertura = "Roubo e Furto";
        String condicoes = "Condições gerais do seguro";

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () ->
                seguroCartaoService.contratarSeguro(1, valorPremio, cobertura, condicoes)
        );

        assertEquals("Este cartão já possui um seguro ativo.", exception.getMessage());
        verify(cartaoDao, times(1)).buscarPorId(1);
        verify(seguroCartaoDao, times(1)).buscarPorCartaoId(1);
        verify(seguroCartaoDao, never()).salvar(any(SeguroCartao.class));
    }

    // --- Testes para buscarPorId ---

    @Test
    @DisplayName("Deve buscar um seguro por ID com sucesso")
    void deveBuscarSeguroPorIdComSucesso() {
        when(seguroCartaoDao.buscarPorId(100)).thenReturn(Optional.of(seguroExistente));

        SeguroCartao seguroEncontrado = seguroCartaoService.buscarPorId(100);

        assertNotNull(seguroEncontrado);
        assertEquals(seguroExistente.getId(), seguroEncontrado.getId());
        verify(seguroCartaoDao, times(1)).buscarPorId(100);
    }

    @Test
    @DisplayName("Deve lançar EntidadeNaoEncontradaException se o seguro por ID não for encontrado")
    void deveLancarExcecaoSeSeguroPorIdNaoEncontrado() {
        when(seguroCartaoDao.buscarPorId(999)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () ->
                seguroCartaoService.buscarPorId(999)
        );

        assertEquals("Seguro com ID 999 não encontrado.", exception.getMessage());
        verify(seguroCartaoDao, times(1)).buscarPorId(999);
    }

    // --- Testes para listarTodos ---

    @Test
    @DisplayName("Deve listar todos os seguros com sucesso")
    void deveListarTodosSegurosComSucesso() {
        SeguroCartao outroSeguro = new SeguroCartao();
        outroSeguro.setId(101);
        outroSeguro.setCartao(cartaoCredito); // Pode ser o mesmo cartão para teste
        outroSeguro.setNumeroApolice("OUTRA-APOLICE-456");
        outroSeguro.setValorPremio(new BigDecimal("30.00"));

        List<SeguroCartao> seguros = Arrays.asList(seguroExistente, outroSeguro);
        when(seguroCartaoDao.listarTodos()).thenReturn(seguros);

        List<SeguroCartao> segurosListados = seguroCartaoService.listarTodos();

        assertNotNull(segurosListados);
        assertEquals(2, segurosListados.size());
        assertTrue(segurosListados.contains(seguroExistente));
        assertTrue(segurosListados.contains(outroSeguro));
        verify(seguroCartaoDao, times(1)).listarTodos();
    }

    @Test
    @DisplayName("Deve retornar lista vazia se não houver seguros")
    void deveRetornarListaVaziaSeNaoHouverSeguros() {
        when(seguroCartaoDao.listarTodos()).thenReturn(List.of()); // Retorna uma lista vazia

        List<SeguroCartao> segurosListados = seguroCartaoService.listarTodos();

        assertNotNull(segurosListados);
        assertTrue(segurosListados.isEmpty());
        verify(seguroCartaoDao, times(1)).listarTodos();
    }

    // --- Testes para cancelarSeguro ---

    @Test
    @DisplayName("Deve cancelar um seguro com sucesso")
    void deveCancelarSeguroComSucesso() {
        when(seguroCartaoDao.buscarPorId(100)).thenReturn(Optional.of(seguroExistente));
        when(seguroCartaoDao.cancelar(100)).thenReturn(1); // Simula 1 linha afetada (sucesso)

        assertDoesNotThrow(() -> seguroCartaoService.cancelarSeguro(100));

        verify(seguroCartaoDao, times(1)).buscarPorId(100);
        verify(seguroCartaoDao, times(1)).cancelar(100);
    }

    @Test
    @DisplayName("Deve lançar EntidadeNaoEncontradaException ao cancelar seguro inexistente")
    void deveLancarExcecaoAoCancelarSeguroInexistente() {
        when(seguroCartaoDao.buscarPorId(999)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () ->
                seguroCartaoService.cancelarSeguro(999)
        );

        assertEquals("Seguro com ID 999 não encontrado.", exception.getMessage());
        verify(seguroCartaoDao, times(1)).buscarPorId(999);
        verify(seguroCartaoDao, never()).cancelar(anyInt()); // Não deve tentar cancelar
    }

    // Este teste é mais para cobertura de caso que não deve acontecer na prática devido ao buscarPorId
    @Test
    @DisplayName("Deve lançar EntidadeNaoEncontradaException se cancelar não afetar linhas (Edge Case)")
    void deveLancarExcecaoSeCancelamentoNaoAfetarLinhas() {
        when(seguroCartaoDao.buscarPorId(100)).thenReturn(Optional.of(seguroExistente));
        when(seguroCartaoDao.cancelar(100)).thenReturn(0); // Simula 0 linhas afetadas

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () ->
                seguroCartaoService.cancelarSeguro(100)
        );

        assertEquals("Seguro com ID 100 não encontrado para cancelamento.", exception.getMessage());
        verify(seguroCartaoDao, times(1)).buscarPorId(100);
        verify(seguroCartaoDao, times(1)).cancelar(100);
    }
}