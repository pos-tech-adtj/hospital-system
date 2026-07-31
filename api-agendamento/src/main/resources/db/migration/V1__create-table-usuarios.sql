CREATE TYPE tipo_usuario_enum AS ENUM (
    'MEDICO',
    'ENFERMEIRO',
    'PACIENTE'
);

CREATE TABLE usuarios
(
    id              UUID PRIMARY KEY,
    nome            VARCHAR(255)      NOT NULL,
    email           VARCHAR(255)      NOT NULL UNIQUE,
    cpf             VARCHAR(11)       NOT NULL UNIQUE,
    senha           VARCHAR(255)      NOT NULL,
    data_nascimento DATE              NOT NULL,
    tipo            tipo_usuario_enum NOT NULL,
    ativo           BOOLEAN           NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_usuarios_tipo ON usuarios (tipo);