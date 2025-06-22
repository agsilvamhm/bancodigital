CREATE TABLE cliente (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    nome VARCHAR(255),
    data_nascimento DATE,
    categoria VARCHAR(50)
);