package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.exception.ResourceNotFoundException;
import com.agsilvamhm.bancodigital.model.Cliente;
import com.agsilvamhm.bancodigital.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente findById(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));
    }

    public Cliente cadastrarCliente(Cliente cliente) {
        validarDados(cliente);
        return clienteRepository.save(cliente);
    }

    private void validarDados(Cliente cliente) {
        // Validar CPF, nome, data de nascimento, endereço etc.
    }


}
