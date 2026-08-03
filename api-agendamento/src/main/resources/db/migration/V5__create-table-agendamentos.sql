CREATE TABLE agendamentos
(
    id BIGSERIAL PRIMARY KEY,

    paciente_id BIGINT NOT NULL,

    medico_id BIGINT NOT NULL,

    data_hora TIMESTAMP NOT NULL,

    status VARCHAR(20) NOT NULL,

    especialidade VARCHAR(120) NOT NULL,

    observacoes VARCHAR(1000),

    criado_por VARCHAR(100) NOT NULL,

    criado_em TIMESTAMP NOT NULL,

    atualizado_em TIMESTAMP
);