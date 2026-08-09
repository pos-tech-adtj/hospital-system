CREATE TYPE status_agendamento_enum AS ENUM (
    'AGENDADA',
    'CANCELADA',
    'REALIZADA',
    'REMARCADA'
);

CREATE TABLE agendamentos
(
    id            UUID PRIMARY KEY,
    medico_id     UUID                    NOT NULL,
    paciente_id   UUID                    NOT NULL,
    data_hora     TIMESTAMP               NOT NULL,
    status        status_agendamento_enum NOT NULL DEFAULT 'AGENDADA',
    especialidade VARCHAR(120)            NOT NULL,
    observacoes   VARCHAR(1000),
    created_at    TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_agendamento_medico
        FOREIGN KEY (medico_id) REFERENCES usuarios (id) ON DELETE RESTRICT,

    CONSTRAINT fk_agendamento_paciente
        FOREIGN KEY (paciente_id) REFERENCES usuarios (id) ON DELETE RESTRICT,

    CONSTRAINT uk_agendamento
        UNIQUE (medico_id, paciente_id, data_hora)
);

CREATE INDEX idx_agendamentos_paciente ON agendamentos (paciente_id);
CREATE INDEX idx_agendamentos_medico ON agendamentos (medico_id);
CREATE INDEX idx_agendamentos_data ON agendamentos (data_hora);
CREATE INDEX idx_agendamentos_status ON agendamentos (status);