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
    tipo_conta VARCHAR(20) NOT NULL,
    taxa_manutencao_mensal DECIMAL(10, 2),
    taxa_rendimento_mensal DECIMAL(5, 4),
    CONSTRAINT conta_cliente_fk FOREIGN KEY (id_cliente) REFERENCES cliente(id)
);

CREATE TABLE movimentacao (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tipo VARCHAR(50) NOT NULL,
    valor DECIMAL(15, 2) NOT NULL,
    data_hora DATETIME NOT NULL,
    id_conta_origem INT,
    id_conta_destino INT,
    descricao VARCHAR(255),
    CONSTRAINT mov_conta_origem_fk FOREIGN KEY (id_conta_origem) REFERENCES conta(id),
    CONSTRAINT mov_conta_destino_fk FOREIGN KEY (id_conta_destino) REFERENCES conta(id)
);

CREATE TABLE cartao (
    id SERIAL PRIMARY KEY,
    id_conta INT NOT NULL,
    numero VARCHAR(19) NOT NULL UNIQUE,
    nome_titular VARCHAR(255) NOT NULL,
    data_validade DATE NOT NULL,
    cvv VARCHAR(4) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    tipo_cartao VARCHAR(20) NOT NULL,
    limite_credito DECIMAL(10, 2),
    limite_diario_debito DECIMAL(10, 2),
    ativo BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_conta FOREIGN KEY (id_conta) REFERENCES conta(id)
);

CREATE TABLE seguro_cartao (
    id SERIAL PRIMARY KEY,
    id_cartao INT NOT NULL UNIQUE,
    numero_apolice VARCHAR(50) NOT NULL UNIQUE,
    data_contratacao TIMESTAMP NOT NULL,
    cobertura TEXT NOT NULL,
    condicoes TEXT NOT NULL,
    valor_premio DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_cartao_credito FOREIGN KEY (id_cartao) REFERENCES cartao(id)
);

CREATE INDEX idx_cartao_id_conta ON cartao(id_conta);