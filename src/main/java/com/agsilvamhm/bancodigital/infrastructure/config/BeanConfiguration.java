package com.agsilvamhm.bancodigital.infrastructure.config;

import com.agsilvamhm.bancodigital.core.port.in.ClienteUseCase;
import com.agsilvamhm.bancodigital.core.port.out.ClienteRepositoryPort;
import com.agsilvamhm.bancodigital.core.port.out.EnderecoRepositoryPort;
import com.agsilvamhm.bancodigital.core.service.ClienteServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ClienteUseCase clienteUseCase(
            ClienteRepositoryPort clienteRepositoryPort,
            EnderecoRepositoryPort enderecoRepositoryPort
    ) {
        return new ClienteServiceImpl(clienteRepositoryPort, enderecoRepositoryPort);
    }
}