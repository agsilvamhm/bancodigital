package com.agsilvamhm.bancodigital.core.port.out;

import com.agsilvamhm.bancodigital.core.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente salvar(Cliente cliente);
    void atualizar(Cliente cliente);
    Optional<Cliente> buscarPorId(Integer id);
    Optional<Cliente> buscarPorCpf(String cpf);
    List<Cliente> listarTodos();
    void deletarPorId(Integer id);
}
