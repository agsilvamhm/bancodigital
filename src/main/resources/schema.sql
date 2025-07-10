CREATE TABLE "endereco" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "rua" VARCHAR(255) NOT NULL,
  "numero" INT,
  "complemento" VARCHAR(100),
  "cidade" VARCHAR(100) NOT NULL,
  "estado" VARCHAR(50) NOT NULL,
  "cep" VARCHAR(9) NOT NULL
);

CREATE TABLE "cliente" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "cpf" VARCHAR(14) UNIQUE NOT NULL,
  "nome" VARCHAR(255) NOT NULL,
  "data_nascimento" DATE NOT NULL,
  "categoria" VARCHAR(20) NOT NULL,
  "id_endereco" INT UNIQUE NOT NULL,
  -- Adicionando a restrição CHECK para simular o ENUM
  CONSTRAINT "check_cliente_categoria" CHECK ("categoria" IN ('COMUM', 'SUPER', 'PREMIUM')),
  CONSTRAINT "fk_cliente_endereco"
    FOREIGN KEY("id_endereco")
    REFERENCES "endereco"("id")
    ON DELETE RESTRICT
);

CREATE TABLE "conta" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "numero" VARCHAR(20) UNIQUE NOT NULL,
  "agencia" VARCHAR(10) NOT NULL,
  "saldo" DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
  "id_cliente" INT NOT NULL,
  CONSTRAINT "fk_conta_cliente"
    FOREIGN KEY("id_cliente")
    REFERENCES "cliente"("id")
    ON DELETE CASCADE
);

CREATE TABLE "conta_corrente" (
  "id_conta" INT PRIMARY KEY,
  "taxa_manutencao_mensal" DECIMAL(10, 2) NOT NULL,
  CONSTRAINT "fk_cc_conta"
    FOREIGN KEY("id_conta")
    REFERENCES "conta"("id")
    ON DELETE CASCADE
);

CREATE TABLE "conta_poupanca" (
  "id_conta" INT PRIMARY KEY,
  "taxa_rendimento_mensal" DECIMAL(5, 4) NOT NULL,
  CONSTRAINT "fk_cp_conta"
    FOREIGN KEY("id_conta")
    REFERENCES "conta"("id")
    ON DELETE CASCADE
);

CREATE TABLE "movimentacao" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "tipo" VARCHAR(20) NOT NULL,
  "valor" DECIMAL(15, 2) NOT NULL,
  "data_hora" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "id_conta_origem" INT,  -- <-- CORRIGIDO: Removido o NOT NULL
  "id_conta_destino" INT,
  "descricao" VARCHAR(255),
  CONSTRAINT "check_movimentacao_tipo" CHECK ("tipo" IN ('DEPOSITO', 'SAQUE', 'TRANSFERENCIA', 'PIX')),
  CONSTRAINT "fk_movimentacao_conta_origem" FOREIGN KEY("id_conta_origem") REFERENCES "conta"("id"),
  CONSTRAINT "fk_movimentacao_conta_destino" FOREIGN KEY("id_conta_destino") REFERENCES "conta"("id")
);

CREATE TABLE "cartao" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "numero" VARCHAR(19) UNIQUE NOT NULL,
  "nome_titular" VARCHAR(255) NOT NULL,
  "data_validade" DATE NOT NULL,
  "cvv" VARCHAR(4) NOT NULL,
  "senha" VARCHAR(255) NOT NULL, -- Lembre-se de armazenar a senha de forma segura (hash).
  "tipo_cartao" VARCHAR(10) NOT NULL,
  "limite_credito" DECIMAL(10, 2),
  "limite_diario_debito" DECIMAL(10, 2),
  "ativo" BOOLEAN NOT NULL DEFAULT true,
  "id_conta" INT NOT NULL,
  -- Adicionando a restrição CHECK para simular o ENUM
  CONSTRAINT "check_cartao_tipo" CHECK ("tipo_cartao" IN ('CREDITO', 'DEBITO')),
  CONSTRAINT "fk_cartao_conta"
    FOREIGN KEY("id_conta")
    REFERENCES "conta"("id")
    ON DELETE CASCADE
);

CREATE TABLE "seguro_cartao" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "numero_apolice" VARCHAR(50) UNIQUE NOT NULL,
  "data_contratacao" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "cobertura" CLOB NOT NULL, -- Usando CLOB para textos longos no H2 (equivalente ao TEXT)
  "condicoes" CLOB,
  "valor_premio" DECIMAL(10, 2) NOT NULL,
  "id_cartao" INT UNIQUE NOT NULL,
  CONSTRAINT "fk_seguro_cartao"
    FOREIGN KEY("id_cartao")
    REFERENCES "cartao"("id")
    ON DELETE CASCADE
);

ALTER TABLE "movimentacao" DROP CONSTRAINT "check_movimentacao_tipo";

ALTER TABLE "movimentacao" ADD CONSTRAINT "check_movimentacao_tipo"
CHECK ("tipo" IN ('DEPOSITO', 'SAQUE', 'TRANSFERENCIA', 'PIX', 'TAXA_MANUTENCAO', 'RENDIMENTO'));

-- Povoar tabelas para os testes  --

INSERT INTO "endereco" (rua, numero, complemento, cidade, estado, cep)
VALUES ('Rua da Consolação', 1500, 'Bloco B, Apto 54', 'São Paulo', 'SP', '01301-100');

INSERT INTO "cliente" (cpf, nome, data_nascimento, categoria, id_endereco)
VALUES ('980.246.810-05', 'Ricardo Souza Costa', '1992-11-10', 'SUPER', 1);

-- Registro 2
INSERT INTO "endereco" (rua, numero, complemento, cidade, estado, cep) VALUES ('Avenida Paulista', 2000, 'Andar 10', 'São Paulo', 'SP', '01310-200');
INSERT INTO "cliente" (cpf, nome, data_nascimento, categoria, id_endereco) VALUES ('123.456.789-10', 'Ana Pereira Lima', '1985-05-20', 'COMUM', 2);

-- Registro 3
INSERT INTO "endereco" (rua, numero, complemento, cidade, estado, cep) VALUES ('Rua das Laranjeiras', 500, 'Casa', 'Rio de Janeiro', 'RJ', '22240-003');
INSERT INTO "cliente" (cpf, nome, data_nascimento, categoria, id_endereco) VALUES ('234.567.890-11', 'Bruno Ferreira Alves', '1998-02-15', 'SUPER', 3);

-- Registro 4
INSERT INTO "endereco" (rua, numero, complemento, cidade, estado, cep) VALUES ('Avenida Boa Viagem', 340, 'Apto 801', 'Recife', 'PE', '51011-000');
INSERT INTO "cliente" (cpf, nome, data_nascimento, categoria, id_endereco) VALUES ('345.678.901-12', 'Carla Dias Martins', '1979-09-30', 'PREMIUM', 4);

-- Registro 5
INSERT INTO "endereco" (rua, numero, complemento, cidade, estado, cep) VALUES ('Rua 24 Horas', 123, NULL, 'Curitiba', 'PR', '80010-010');
INSERT INTO "cliente" (cpf, nome, data_nascimento, categoria, id_endereco) VALUES ('456.789.012-13', 'Daniel Oliveira Santos', '2001-07-11', 'COMUM', 5);

-- Registro 6
INSERT INTO "endereco" (rua, numero, complemento, cidade, estado, cep) VALUES ('Praça da Liberdade', 10, 'Prédio Histórico', 'Belo Horizonte', 'MG', '30140-010');
INSERT INTO "cliente" (cpf, nome, data_nascimento, categoria, id_endereco) VALUES ('567.890.123-14', 'Elisa Gomes Ribeiro', '1995-12-01', 'SUPER', 6);

-- Cliente 1 (ID: 1)
INSERT INTO "conta" (numero, agencia, saldo, id_cliente) VALUES ('1001-1', '0001', 1500.75, 1);
INSERT INTO "conta_corrente" (id_conta, taxa_manutencao_mensal) VALUES (1, 25.00); -- id_conta = 1
INSERT INTO "conta" (numero, agencia, saldo, id_cliente) VALUES ('1001-9', '0001', 10250.00, 1);
INSERT INTO "conta_poupanca" (id_conta, taxa_rendimento_mensal) VALUES (2, 0.0050); -- id_conta = 2

-- Cliente 2 (ID: 2)
INSERT INTO "conta" (numero, agencia, saldo, id_cliente) VALUES ('2002-2', '0001', 850.00, 2);
INSERT INTO "conta_corrente" (id_conta, taxa_manutencao_mensal) VALUES (3, 20.00); -- id_conta = 3
INSERT INTO "conta" (numero, agencia, saldo, id_cliente) VALUES ('2002-8', '0001', 25000.80, 2);
INSERT INTO "conta_poupanca" (id_conta, taxa_rendimento_mensal) VALUES (4, 0.0055); -- id_conta = 4

-- Cliente 3 (ID: 3)
INSERT INTO "conta" (numero, agencia, saldo, id_cliente) VALUES ('3003-3', '0001', 3200.50, 3);
INSERT INTO "conta_corrente" (id_conta, taxa_manutencao_mensal) VALUES (5, 25.00); -- id_conta = 5
INSERT INTO "conta" (numero, agencia, saldo, id_cliente) VALUES ('3003-7', '0001', 12345.67, 3);
INSERT INTO "conta_poupanca" (id_conta, taxa_rendimento_mensal) VALUES (6, 0.0060); -- id_conta = 6

-- Cliente 4 (ID: 4)
INSERT INTO "conta" (numero, agencia, saldo, id_cliente) VALUES ('4004-4', '0001', 15000.00, 4);
INSERT INTO "conta_corrente" (id_conta, taxa_manutencao_mensal) VALUES (7, 35.00); -- id_conta = 7
INSERT INTO "conta" (numero, agencia, saldo, id_cliente) VALUES ('4004-6', '0001', 75000.00, 4);
INSERT INTO "conta_poupanca" (id_conta, taxa_rendimento_mensal) VALUES (8, 0.0065); -- id_conta = 8

-- Cliente 5 (ID: 5)
INSERT INTO "conta" (numero, agencia, saldo, id_cliente) VALUES ('5005-5', '0001', 980.20, 5);
INSERT INTO "conta_corrente" (id_conta, taxa_manutencao_mensal) VALUES (9, 15.00); -- id_conta = 9
INSERT INTO "conta" (numero, agencia, saldo, id_cliente) VALUES ('5005-4', '0001', 5400.00, 5);
INSERT INTO "conta_poupanca" (id_conta, taxa_rendimento_mensal) VALUES (10, 0.0050); -- id_conta = 10
