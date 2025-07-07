package com.agsilvamhm.bancodigital;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Este é o teste de fumaça (smoke test) principal da aplicação.
 * Ele verifica se o contexto do Spring Boot consegue ser inicializado
 * corretamente, carregando todos os beans e configurações.
 */
@SpringBootTest // A anotação mais importante: carrega a aplicação Spring para o teste.
@DisplayName("Teste de Carga do Contexto da Aplicação")
class BancodigitalApplicationTests {

    // Injeta o próprio contexto da aplicação para provar que ele existe.
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Verifica se o contexto da aplicação carrega sem erros")
    void contextLoads() {
        // O teste real acontece ANTES deste método ser executado, quando o @SpringBootTest
        // tenta carregar o contexto. Se chegar até aqui, já é um bom sinal.

        // A verificação final (Assert) é para garantir que o contexto não é nulo.
        System.out.println("Iniciando teste de carga do contexto...");

        assertThat(applicationContext).isNotNull();

        System.out.println("Contexto da aplicação carregado com sucesso! ✅");
    }
}