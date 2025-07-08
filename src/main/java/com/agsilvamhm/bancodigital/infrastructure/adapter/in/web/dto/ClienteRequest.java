package com.agsilvamhm.bancodigital.infrastructure.adapter.in.web.dto;

import com.agsilvamhm.bancodigital.model.CategoriaCliente;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public class ClienteRequest {
    @CPF(message = "CPF inválido.")
    @NotNull(message = "O CPF não pode ser nulo.")
    private String cpf;

    @NotNull(message = "O nome não pode ser nulo.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre {min} e {max} caracteres.")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ ]+$", message = "O nome deve conter apenas letras e espaços.")
    private String nome;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    @NotNull(message = "O endereço é obrigatório.")
    @Valid
    private EnderecoDTO endereco;

    @NotNull(message = "A categoria é obrigatória.")
    private CategoriaCliente categoria;

    // Getters e Setters
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public EnderecoDTO getEndereco() { return endereco; }
    public void setEndereco(EnderecoDTO endereco) { this.endereco = endereco; }
    public CategoriaCliente getCategoria() { return categoria; }
    public void setCategoria(CategoriaCliente categoria) { this.categoria = categoria; }
}
