CREATE TABLE medicos
(
    id            UUID PRIMARY KEY,
    usuario_id    UUID         NOT NULL UNIQUE,
    crm           VARCHAR(20)  NOT NULL UNIQUE,
    especialidade VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE INDEX idx_medicos_especialidade ON medicos (especialidade);