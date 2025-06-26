CREATE TABLE endereco(
    id INT PRIMARY KEY AUTO_INCREMENT,
    rua VARCHAR(50) NOT NULL,
    numero INT NOT NULL,
    complemento VARCHAR(50) NOT NULL,
    cidade VARCHAR(50) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    cep VARCHAR(8) NOT NULL
);

CREATE TABLE cliente (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    nome VARCHAR(255),
    data_nascimento DATE,
    categoria VARCHAR(50),
    id_endereco int,
    constraint cliente_endereco FOREIGN KEY (id_endereco) REFERENCES endereco(id)
);

CREATE TABLE conta (
    id INT PRIMARY KEY AUTO_INCREMENT,
    numero VARCHAR(20) NOT NULL UNIQUE,
    agencia VARCHAR(10) NOT NULL,
    saldo DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    id_cliente INT NOT NULL,

    -- Coluna para diferenciar os tipos de conta
    tipo_conta VARCHAR(20) NOT NULL, -- 'CORRENTE' ou 'POUPANCA'

    -- Colunas específicas (podem ser nulas dependendo do tipo)
    taxa_manutencao_mensal DECIMAL(10, 2), -- Apenas para Conta Corrente
    taxa_rendimento_mensal DECIMAL(5, 4),   -- Apenas para Conta Poupança

    CONSTRAINT conta_cliente_fk FOREIGN KEY (id_cliente) REFERENCES cliente(id)
);

-- Tabela para registrar todas as movimentações
CREATE TABLE movimentacao (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tipo VARCHAR(50) NOT NULL, -- 'DEPOSITO', 'SAQUE', 'PIX', etc.
    valor DECIMAL(15, 2) NOT NULL,
    data_hora DATETIME NOT NULL,
    id_conta_origem INT,
    id_conta_destino INT,
    descricao VARCHAR(255),

    CONSTRAINT mov_conta_origem_fk FOREIGN KEY (id_conta_origem) REFERENCES conta(id),
    CONSTRAINT mov_conta_destino_fk FOREIGN KEY (id_conta_destino) REFERENCES conta(id)
);
