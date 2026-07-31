CREATE TYPE status_consulta_enum AS ENUM (
    'AGENDADA',
    'CANCELADA',
    'REALIZADA',
    'REMARCADA'
);

CREATE TABLE consultas
(
    id            UUID PRIMARY KEY,
    medico_id     UUID                 NOT NULL,
    paciente_id   UUID                 NOT NULL,
    data_consulta TIMESTAMP            NOT NULL,
    motivo        VARCHAR(255)         NOT NULL,
    descricao     TEXT,
    status        status_consulta_enum NOT NULL DEFAULT 'AGENDADA',
    created_at    TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_consulta_medico
        FOREIGN KEY (medico_id)
            REFERENCES usuarios (id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_consulta_paciente
        FOREIGN KEY (paciente_id)
            REFERENCES usuarios (id)
            ON DELETE RESTRICT,

    CONSTRAINT uk_consulta
        UNIQUE (medico_id, paciente_id, data_consulta)
);

CREATE INDEX idx_consultas_medico ON consultas (medico_id);
CREATE INDEX idx_consultas_paciente ON consultas (paciente_id);
CREATE INDEX idx_consultas_data ON consultas (data_consulta);
CREATE INDEX idx_consultas_status ON consultas (status);