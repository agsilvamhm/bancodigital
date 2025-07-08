package com.agsilvamhm.bancodigital.core.service;

import com.agsilvamhm.bancodigital.core.domain.exceptions.CpfDuplicadoException;
import com.agsilvamhm.bancodigital.core.domain.exceptions.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.core.domain.model.Cliente;
import com.agsilvamhm.bancodigital.core.domain.model.Endereco;
import com.agsilvamhm.bancodigital.core.port.in.ClienteUseCase;
import com.agsilvamhm.bancodigital.core.port.out.ClienteRepositoryPort;
import com.agsilvamhm.bancodigital.core.port.out.EnderecoRepositoryPort;

import java.util.List;
import java.util.Objects;

public class ClienteServiceImpl implements ClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;
    private final EnderecoRepositoryPort enderecoRepository;

    public ClienteServiceImpl(ClienteRepositoryPort clienteRepository, EnderecoRepositoryPort enderecoRepository) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
    }

    @Override
    public Cliente criarCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "O objeto cliente não pode ser nulo.");
        Objects.requireNonNull(cliente.getEndereco(), "O cliente deve possuir um endereço.");

        clienteRepository.buscarPorCpf(cliente.getCpf()).ifPresent(c -> {
            throw new CpfDuplicadoException("CPF já está cadastrado.");
        });

        String cepLimpo = cliente.getEndereco().getCep().replaceAll("[^0-9]", "");
        cliente.getEndereco().setCep(cepLimpo);

        Endereco enderecoSalvo = enderecoRepository.salvar(cliente.getEndereco());
        cliente.setEndereco(enderecoSalvo);

        return clienteRepository.salvar(cliente);
    }

    @Override
    public Cliente atualizarCliente(Integer id, Cliente clienteAtualizado) {
        Objects.requireNonNull(id, "O ID do cliente não pode ser nulo.");
        Objects.requireNonNull(clienteAtualizado, "O objeto cliente não pode ser nulo.");

        Cliente clienteExistente = buscarPorId(id);

        Endereco enderecoParaAtualizar = clienteAtualizado.getEndereco();
        enderecoParaAtualizar.setId(clienteExistente.getEndereco().getId());
        enderecoRepository.atualizar(enderecoParaAtualizar);

        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());
        clienteExistente.setCategoria(clienteAtualizado.getCategoria());
        clienteExistente.setEndereco(enderecoParaAtualizar);

        clienteRepository.atualizar(clienteExistente);
        return clienteExistente;
    }

    @Override
    public Cliente buscarPorId(Integer id) {
        Objects.requireNonNull(id, "O ID do cliente não pode ser nulo.");
        return clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente com ID " + id + " não encontrado."));
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepository.listarTodos();
    }

    @Override
    public void deletarPorId(Integer id) {
        Objects.requireNonNull(id, "O ID do cliente não pode ser nulo.");
        Cliente cliente = buscarPorId(id);

        clienteRepository.deletarPorId(id);
        enderecoRepository.deletar(cliente.getEndereco().getId());
    }
}