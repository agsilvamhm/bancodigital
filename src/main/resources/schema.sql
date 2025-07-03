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