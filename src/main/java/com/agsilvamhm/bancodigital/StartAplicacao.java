package com.agsilvamhm.bancodigital;

import com.agsilvamhm.bancodigital.model.Cliente;
import com.agsilvamhm.bancodigital.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartAplicacao implements CommandLineRunner {
    @Autowired
    private ClienteRepository repository;
    @Override
    public void run(String... args) throws Exception {
        Cliente cliente = new Cliente();
        cliente.setCpf("87946858434");
        cliente.setNome("Adalberto Gonçalves da Silva");
        repository.save(cliente);

        for(Cliente c: repository.findAll()){
            System.out.println(c);
        }

    }
}
