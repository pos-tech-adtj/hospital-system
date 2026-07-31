CREATE TYPE status_enum AS ENUM ('PENDENTE', 'ENVIADA', 'FALHA');

CREATE TABLE notificacoes
(
    id               UUID PRIMARY KEY,
    paciente_id      UUID         NOT NULL,
    paciente_nome    VARCHAR(255) NOT NULL,
    paciente_email   VARCHAR(255) NOT NULL,
    medico_nome      VARCHAR(255),
    data_consulta    TIMESTAMP    NOT NULL,
    motivo           VARCHAR(255),
    status           status_enum  NOT NULL DEFAULT 'PENDENTE',
    data_notificacao TIMESTAMP,
    tentativas_envio INTEGER      NOT NULL DEFAULT 0,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_paciente_id ON notificacoes (paciente_id);
CREATE INDEX idx_paciente_email ON notificacoes (paciente_email);
CREATE INDEX idx_status ON notificacoes (status);
CREATE INDEX idx_data_consulta ON notificacoes (data_consulta);
CREATE INDEX idx_data_notificacao ON notificacoes (data_notificacao);