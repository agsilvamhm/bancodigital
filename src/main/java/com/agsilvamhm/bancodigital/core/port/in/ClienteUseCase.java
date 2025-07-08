package com.agsilvamhm.bancodigital.core.port.in;

import com.agsilvamhm.bancodigital.core.domain.model.Cliente;

import java.util.List;

public interface ClienteUseCase {
    Cliente criarCliente(Cliente cliente);
    Cliente atualizarCliente(Integer id, Cliente cliente);
    Cliente buscarPorId(Integer id);
    List<Cliente> listarTodos();
    void deletarPorId(Integer id);
}