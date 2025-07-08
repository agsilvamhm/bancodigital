package com.agsilvamhm.bancodigital.infrastructure.adapter.in.web.mapper;

import com.agsilvamhm.bancodigital.core.domain.model.Cliente;
import com.agsilvamhm.bancodigital.core.domain.model.Endereco;
import com.agsilvamhm.bancodigital.infrastructure.adapter.in.web.dto.ClienteRequest;
import com.agsilvamhm.bancodigital.infrastructure.adapter.in.web.dto.ClienteResponse;
import com.agsilvamhm.bancodigital.infrastructure.adapter.in.web.dto.EnderecoDTO;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toDomain(ClienteRequest dto) {
        Cliente cliente = new Cliente();
        cliente.setCpf(dto.getCpf());
        cliente.setNome(dto.getNome());
        cliente.setDataNascimento(dto.getDataNascimento());
        cliente.setCategoria(dto.getCategoria());
        cliente.setEndereco(toDomain(dto.getEndereco()));
        return cliente;
    }

    public Endereco toDomain(EnderecoDTO dto) {
        Endereco endereco = new Endereco();
        endereco.setRua(dto.getRua());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setCep(dto.getCep());
        return endereco;
    }

    public ClienteResponse toResponse(Cliente domain) {
        ClienteResponse response = new ClienteResponse();
        response.setId(domain.getId());
        response.setCpf(domain.getCpf());
        response.setNome(domain.getNome());
        response.setDataNascimento(domain.getDataNascimento());
        response.setCategoria(domain.getCategoria());
        response.setEndereco(toResponse(domain.getEndereco()));
        response.setContas(domain.getContas());
        return response;
    }

    public EnderecoDTO toResponse(Endereco domain) {
        EnderecoDTO dto = new EnderecoDTO();
        dto.setRua(domain.getRua());
        dto.setNumero(domain.getNumero());
        dto.setComplemento(domain.getComplemento());
        dto.setCidade(domain.getCidade());
        dto.setEstado(domain.getEstado());
        dto.setCep(domain.getCep());
        return dto;
    }
}
