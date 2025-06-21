package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.controller.exception.GlobalExceptionHandler;
import com.agsilvamhm.bancodigital.entity.Cliente;
import com.agsilvamhm.bancodigital.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente findById(Integer id) {
        return null ; //clienteRepository.findById(id);;
    }

    public Cliente cadastrarCliente(Cliente cliente) {
        validarDados(cliente);
        return clienteRepository.save(cliente);
    }

    private void validarDados(Cliente cliente) {
        // Validar CPF, nome, data de nascimento, endereço etc.
    }


}
