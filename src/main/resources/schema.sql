-- Limpa as tabelas existentes na ordem inversa de dependência para evitar erros de chave estrangeira
DROP TABLE IF EXISTS "seguro_cartao";
DROP TABLE IF EXISTS "movimentacao"; -- Movimentacao agora vem antes de Cartao no DROP
DROP TABLE IF EXISTS "cartao";
DROP TABLE IF EXISTS "conta";
DROP TABLE IF EXISTS "cliente";
DROP TABLE IF EXISTS "endereco";

---
-- 1. Tabela de Endereços (Não depende de nada)
CREATE TABLE "endereco" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "rua" VARCHAR(255) NOT NULL,
  "numero" INT,
  "complemento" VARCHAR(100),
  "cidade" VARCHAR(100) NOT NULL,
  "estado" VARCHAR(50) NOT NULL,
  "cep" VARCHAR(9) NOT NULL
);

---
-- 2. Tabela de Clientes (Depende de endereco)
CREATE TABLE "cliente" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "cpf" VARCHAR(14) UNIQUE NOT NULL,
  "nome" VARCHAR(255) NOT NULL,
  "data_nascimento" DATE NOT NULL,
  "categoria" VARCHAR(20) NOT NULL,
  "id_endereco" INT NOT NULL,
  CONSTRAINT "check_cliente_categoria" CHECK ("categoria" IN ('COMUM', 'SUPER', 'PREMIUM')),
  CONSTRAINT "fk_cliente_endereco"
    FOREIGN KEY("id_endereco")
    REFERENCES "endereco"("id")
    ON DELETE RESTRICT
);

---
-- 3. Tabela de Contas (Depende de cliente)
CREATE TABLE "conta" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "tipo_conta" VARCHAR(10) NOT NULL CHECK ("tipo_conta" IN ('CORRENTE', 'POUPANCA')),
  "numero" VARCHAR(20) UNIQUE NOT NULL,
  "agencia" VARCHAR(10) NOT NULL,
  "saldo" DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
  "id_cliente" INT NOT NULL,
  CONSTRAINT "fk_conta_cliente"
    FOREIGN KEY("id_cliente")
    REFERENCES "cliente"("id")
    ON DELETE CASCADE
);

---
-- 4. Tabela de Cartões (Depende de conta) - NOVA POSIÇÃO
CREATE TABLE "cartao" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "numero" VARCHAR(19) UNIQUE NOT NULL,
  "nome_titular" VARCHAR(255) NOT NULL,
  "data_validade" DATE NOT NULL,
  "cvv" VARCHAR(4) NOT NULL,
  "senha" VARCHAR(255) NOT NULL,
  "tipo_cartao" VARCHAR(10) NOT NULL,
  "limite_credito" DECIMAL(10, 2),
  "limite_diario_debito" DECIMAL(10, 2),
  "ativo" BOOLEAN NOT NULL DEFAULT true,
  "id_conta" INT NOT NULL,
  CONSTRAINT "check_cartao_tipo" CHECK ("tipo_cartao" IN ('CREDITO', 'DEBITO')),
  CONSTRAINT "fk_cartao_conta"
    FOREIGN KEY("id_conta")
    REFERENCES "conta"("id")
    ON DELETE CASCADE
);

---
-- 5. Tabela de Movimentações (Depende de conta E cartao) - NOVA POSIÇÃO
CREATE TABLE "movimentacao" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "tipo" VARCHAR(20) NOT NULL,
  "valor" DECIMAL(15, 2) NOT NULL,
  "data_hora" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "id_conta_origem" INT NULL,
  "id_conta_destino" INT NULL,
  "id_cartao" INT NULL,
  "descricao" VARCHAR(255),
  CONSTRAINT "check_movimentacao_tipo" CHECK ("tipo" IN ('DEPOSITO', 'SAQUE', 'TRANSFERENCIA', 'PIX', 'TAXA_MANUTENCAO', 'RENDIMENTO', 'COMPRA_CREDITO', 'PAGAMENTO_FATURA')),
  CONSTRAINT "fk_movimentacao_conta_origem" FOREIGN KEY("id_conta_origem") REFERENCES "conta"("id"),
  CONSTRAINT "fk_movimentacao_conta_destino" FOREIGN KEY("id_conta_destino") REFERENCES "conta"("id"),
  CONSTRAINT "fk_movimentacao_cartao" FOREIGN KEY("id_cartao") REFERENCES "cartao"("id")
);

---
-- 6. Tabela de Seguro de Cartão (Depende de cartao)
CREATE TABLE "seguro_cartao" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "numero_apolice" VARCHAR(50) UNIQUE NOT NULL,
  "data_contratacao" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "cobertura" CLOB NOT NULL,
  "condicoes" CLOB,
  "valor_premio" DECIMAL(10, 2) NOT NULL,
  "id_cartao" INT UNIQUE NOT NULL,
  CONSTRAINT "fk_seguro_cartao"
    FOREIGN KEY("id_cartao")
    REFERENCES "cartao"("id")
    ON DELETE CASCADE
);

---
-- Povoar tabelas para os testes --
-- O INSERT INTO funciona na mesma ordem de criação das tabelas

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
INSERT INTO "conta" (tipo_conta, numero, agencia, saldo, id_cliente) VALUES ('CORRENTE', '1001-1', '0001', 1500.75, 1);
INSERT INTO "conta" (tipo_conta, numero, agencia, saldo, id_cliente) VALUES ('POUPANCA', '1001-9', '0001', 10250.00, 1);

-- Cliente 2 (ID: 2)
INSERT INTO "conta" (tipo_conta, numero, agencia, saldo, id_cliente) VALUES ('CORRENTE', '2002-2', '0001', 850.00, 2);
INSERT INTO "conta" (tipo_conta, numero, agencia, saldo, id_cliente) VALUES ('POUPANCA', '2002-8', '0001', 25000.80, 2);

-- Cliente 3 (ID: 3)
INSERT INTO "conta" (tipo_conta, numero, agencia, saldo, id_cliente) VALUES ('CORRENTE', '3003-3', '0001', 3200.50, 3);
INSERT INTO "conta" (tipo_conta, numero, agencia, saldo, id_cliente) VALUES ('POUPANCA', '3003-7', '0001', 12345.67, 3);

-- Cliente 4 (ID: 4)
INSERT INTO "conta" (tipo_conta, numero, agencia, saldo, id_cliente) VALUES ('CORRENTE', '4004-4', '0001', 15000.00, 4);
INSERT INTO "conta" (tipo_conta, numero, agencia, saldo, id_cliente) VALUES ('POUPANCA', '4004-6', '0001', 75000.00, 4);

-- Cliente 5 (ID: 5)
INSERT INTO "conta" (tipo_conta, numero, agencia, saldo, id_cliente) VALUES ('CORRENTE', '5005-5', '0001', 980.20, 5);
INSERT INTO "conta" (tipo_conta, numero, agencia, saldo, id_cliente) VALUES ('POUPANCA', '5005-4', '0001', 5400.00, 5);

-- Povoar a tabela Cartão

-- Cartão de Crédito para Cliente 1 (Conta CORRENTE - ID: 1)
INSERT INTO "cartao" (numero, nome_titular, data_validade, cvv, senha, tipo_cartao, limite_credito, limite_diario_debito, ativo, id_conta)
VALUES ('4000-1234-5678-9010', 'RICARDO S. COSTA', '2028-12-31', '123', 'senha123', 'CREDITO', 5000.00, NULL, true, 1);

-- Cartão de Débito para Cliente 1 (Conta POUPANCA - ID: 2)
INSERT INTO "cartao" (numero, nome_titular, data_validade, cvv, senha, tipo_cartao, limite_credito, limite_diario_debito, ativo, id_conta)
VALUES ('5000-1122-3344-5566', 'RICARDO S. COSTA', '2027-11-30', '456', 'senha456', 'DEBITO', NULL, 1000.00, true, 2);

-- Cartão de Crédito para Cliente 2 (Conta CORRENTE - ID: 3)
INSERT INTO "cartao" (numero, nome_titular, data_validade, cvv, senha, tipo_cartao, limite_credito, limite_diario_debito, ativo, id_conta)
VALUES ('4000-9876-5432-1098', 'ANA P. LIMA', '2029-01-31', '789', 'senha789', 'CREDITO', 3000.00, NULL, true, 3);

-- Cartão de Débito para Cliente 3 (Conta CORRENTE - ID: 5)
INSERT INTO "cartao" (numero, nome_titular, data_validade, cvv, senha, tipo_cartao, limite_credito, limite_diario_debito, ativo, id_conta)
VALUES ('5000-9988-7766-5544', 'BRUNO F. ALVES', '2026-06-30', '101', 'senha101', 'DEBITO', NULL, 800.00, true, 5);

-- Cartão de Crédito para Cliente 4 (Conta CORRENTE - ID: 7)
INSERT INTO "cartao" (numero, nome_titular, data_validade, cvv, senha, tipo_cartao, limite_credito, limite_diario_debito, ativo, id_conta)
VALUES ('4000-1111-2222-3333', 'CARLA D. MARTINS', '2030-03-31', '202', 'senha202', 'CREDITO', 10000.00, NULL, true, 7);

---

-- Povoar a tabela Movimentação

-- Movimentações para Cliente 1 (Ricardo Souza Costa)
-- ID da Conta Corrente: 1, ID da Conta Poupança: 2
-- ID do Cartão de Crédito: 1, ID do Cartão de Débito: 2

-- Depósito na Conta Corrente
INSERT INTO "movimentacao" (tipo, valor, data_hora, id_conta_destino, descricao)
VALUES ('DEPOSITO', 200.00, CURRENT_TIMESTAMP, 1, 'Depósito em dinheiro');

-- Compra com Cartão de Crédito (Cartão ID 1)
INSERT INTO "movimentacao" (tipo, valor, data_hora, id_conta_origem, id_cartao, descricao)
VALUES ('COMPRA_CREDITO', 75.50, CURRENT_TIMESTAMP, 1, 1, 'Compra em supermercado');

-- Saque na Conta Corrente
INSERT INTO "movimentacao" (tipo, valor, data_hora, id_conta_origem, descricao)
VALUES ('SAQUE', 100.00, CURRENT_TIMESTAMP, 1, 'Saque em caixa eletrônico');

-- Pix da Conta Corrente para a Conta Poupança
INSERT INTO "movimentacao" (tipo, valor, data_hora, id_conta_origem, id_conta_destino, descricao)
VALUES ('PIX', 300.00, CURRENT_TIMESTAMP, 1, 2, 'Transferência PIX para poupança');

-- Rendimento na Conta Poupança
INSERT INTO "movimentacao" (tipo, valor, data_hora, id_conta_destino, descricao)
VALUES ('RENDIMENTO', 15.20, CURRENT_TIMESTAMP, 2, 'Rendimento mensal da poupança');

-- Pagamento de Fatura do Cartão de Crédito (Cartão ID 1)
INSERT INTO "movimentacao" (tipo, valor, data_hora, id_conta_origem, id_cartao, descricao)
VALUES ('PAGAMENTO_FATURA', 75.50, CURRENT_TIMESTAMP, 1, 1, 'Pagamento fatura cartão de crédito');

-- Movimentações para Cliente 2 (Ana Pereira Lima)
-- ID da Conta Corrente: 3, ID da Conta Poupança: 4
-- ID do Cartão de Crédito: 3

-- Compra com Cartão de Crédito (Cartão ID 3)
INSERT INTO "movimentacao" (tipo, valor, data_hora, id_conta_origem, id_cartao, descricao)
VALUES ('COMPRA_CREDITO', 120.00, CURRENT_TIMESTAMP, 3, 3, 'Compra online de roupas');

-- Transferência da Conta Corrente para outra conta (Não especificada)
INSERT INTO "movimentacao" (tipo, valor, data_hora, id_conta_origem, descricao)
VALUES ('TRANSFERENCIA', 50.00, CURRENT_TIMESTAMP, 3, 'Transferência para amigo');

-- Movimentações para Cliente 3 (Bruno Ferreira Alves)
-- ID da Conta Corrente: 5, ID da Conta Poupança: 6
-- ID do Cartão de Débito: 4

-- Compra com Cartão de Débito (Cartão ID 4)
INSERT INTO "movimentacao" (tipo, valor, data_hora, id_conta_origem, id_cartao, descricao)
VALUES ('SAQUE', 35.00, CURRENT_TIMESTAMP, 5, 4, 'Compra em padaria com cartão de débito');

-- Movimentações para Cliente 4 (Carla Dias Martins)
-- ID da Conta Corrente: 7, ID da Conta Poupança: 8
-- ID do Cartão de Crédito: 5

-- Compra com Cartão de Crédito (Cartão ID 5)
INSERT INTO "movimentacao" (tipo, valor, data_hora, id_conta_origem, id_cartao, descricao)
VALUES ('COMPRA_CREDITO', 500.00, CURRENT_TIMESTAMP, 7, 5, 'Parcelamento de eletrônicos');