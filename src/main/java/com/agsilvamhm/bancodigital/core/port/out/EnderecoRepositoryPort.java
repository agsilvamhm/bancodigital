package com.agsilvamhm.bancodigital.core.port.out;

import com.agsilvamhm.bancodigital.core.domain.model.Endereco;

import java.util.Optional;

public interface EnderecoRepositoryPort {
    Endereco salvar(Endereco endereco);
    void atualizar(Endereco endereco);
    Optional<Endereco> buscarPorId(Integer id);
    void deletar(Integer id);
}