package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.entity.Cliente;
import com.agsilvamhm.bancodigital.repository.ClienteDao;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteDao clienteDao;

    public ClienteService(ClienteDao clienteDao){
        this.clienteDao = clienteDao;
    }

    public void criar(Cliente cliente, JwtAuthenticationToken token){
            clienteDao.salvar(cliente);
    }

    public Cliente getClienteId(Integer id){
        return clienteDao.buscarPorId(id);
    }

    public void deletar(Integer id){
        clienteDao.deletar(id);
    }

    public List listarClientes(){
        List<Cliente> clientes = clienteDao.listarTodos();
        return clientes;
    }

    public void atualizar(Cliente cliente){
        clienteDao.atualizar(cliente);
    }
}
