package com.agsilvamhm.bancodigital.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * POJO que representa a entidade ContaCorrente.
 * Esta classe herda de Conta e adiciona atributos específicos
 * da tabela 'conta_corrente'.
 */
public class ContaCorrente extends Conta {

    private BigDecimal taxaManutencao;

    /**
     * Construtor padrão.
     */
    public ContaCorrente() {
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
     * @param taxaManutencao A taxa de manutenção específica da conta corrente.
     */
    public ContaCorrente(Integer id, Integer idCliente, String agencia, String numeroConta, BigDecimal saldo, LocalDateTime dataAbertura, BigDecimal taxaManutencao) {
        // Chama o construtor da classe pai para inicializar os atributos herdados
        super(id, idCliente, agencia, numeroConta, saldo, dataAbertura);
        this.taxaManutencao = taxaManutencao;
    }

    // --- Getter e Setter específico ---

    public BigDecimal getTaxaManutencao() {
        return taxaManutencao;
    }

    public void setTaxaManutencao(BigDecimal taxaManutencao) {
        this.taxaManutencao = taxaManutencao;
    }

    // --- Sobrescrita do toString ---

    /**
     * Retorna uma representação em String do objeto, incluindo os campos da classe pai.
     */
    @Override
    public String toString() {
        // Remove o '}' do final da string da superclasse para adicionar o novo campo.
        String paiToString = super.toString().substring(0, super.toString().length() - 1);
        return paiToString + ", taxaManutencao=" + taxaManutencao + '}';
    }
}
