package com.agsilvamhm.bancodigital.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * POJO que representa a entidade ContaPoupanca.
 * Esta classe herda de Conta e adiciona atributos específicos
 * da tabela 'conta_poupanca'.
 */
public class ContaPoupanca extends Conta {

    private BigDecimal taxaRendimento;

    /**
     * Construtor padrão.
     */
    public ContaPoupanca() {
        // A chamada super() para o construtor da classe pai é implícita.
    }

    /**
     * Construtor completo para facilitar a criação de instâncias.
     *
     * @param id             O ID da conta (herdado).
     * @param idCliente      O ID do cliente (herdado).
     * @param agencia        O número da agência (herdado).
     * @param numeroConta    O número da conta (herdado).
     * @param saldo          O saldo da conta (herdado).
     * @param dataAbertura   A data de abertura da conta (herdado).
     * @param taxaRendimento A taxa de rendimento específica da conta poupança.
     */
    public ContaPoupanca(Integer id, Integer idCliente, String agencia, String numeroConta, BigDecimal saldo, LocalDateTime dataAbertura, BigDecimal taxaRendimento) {
        // Chama o construtor da classe pai para inicializar os atributos herdados
        super(id, idCliente, agencia, numeroConta, saldo, dataAbertura);
        this.taxaRendimento = taxaRendimento;
    }

    // --- Getter e Setter específico ---

    public BigDecimal getTaxaRendimento() {
        return taxaRendimento;
    }

    public void setTaxaRendimento(BigDecimal taxaRendimento) {
        this.taxaRendimento = taxaRendimento;
    }

    // --- Sobrescrita do toString ---

    /**
     * Retorna uma representação em String do objeto, incluindo os campos da classe pai.
     */
    @Override
    public String toString() {
        // Remove o '}' do final da string da superclasse para adicionar o novo campo.
        String paiToString = super.toString().substring(0, super.toString().length() - 1);
        return paiToString + ", taxaRendimento=" + taxaRendimento + '}';
    }
}