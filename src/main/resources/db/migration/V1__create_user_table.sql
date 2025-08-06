CREATE TYPE usuario_status AS ENUM (
    'ATIVO',
    'INATIVO',
    'BLOQUEADO',
    'PENDENTE_VERIFICACAO'
);

CREATE TABLE IF NOT EXISTS "usuario" (
    usuario_id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status usuario_status NOT NULL
);