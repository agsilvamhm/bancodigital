CREATE TABLE cliente (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    nome VARCHAR(255),
    data_nascimento DATE,
    categoria VARCHAR(50)
);

CREATE TABLE conta (
    id int AUTO_INCREMENT PRIMARY KEY,
    id_cliente int NOT NULL,
    agencia VARCHAR(10) NOT NULL,
    numero_conta VARCHAR(20) NOT NULL,
    saldo DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    data_abertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_agencia_numero UNIQUE (agencia, numero_conta),

    CONSTRAINT fk_contas_clientes FOREIGN KEY (id_cliente) REFERENCES cliente(id)
);

CREATE TABLE conta_corrente (
    conta_id int PRIMARY KEY,
    taxa_manutencao DECIMAL(10, 2) NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_cc_contas FOREIGN KEY (conta_id) REFERENCES conta(id) ON DELETE CASCADE
);

CREATE TABLE conta_poupanca (
    conta_id int PRIMARY KEY,
    taxa_rendimento DECIMAL(5, 4) NOT NULL,

    CONSTRAINT fk_cp_contas FOREIGN KEY (conta_id) REFERENCES conta(id) ON DELETE CASCADE
);
