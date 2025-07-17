package com.agsilvamhm.bancodigital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BancodigitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(BancodigitalApplication.class, args);
	}

	@Bean // Esta anotação informa ao Spring para gerenciar este método como um bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
