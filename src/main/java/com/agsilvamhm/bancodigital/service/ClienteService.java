package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.model.Cliente;
import com.agsilvamhm.bancodigital.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente cadastrarCliente(Cliente cliente) {
        validarDados(cliente);
        return clienteRepository.save(cliente);
    }

    private void validarDados(Cliente cliente) {
        // Validar CPF, nome, data de nascimento, endereço etc.
    }
}
