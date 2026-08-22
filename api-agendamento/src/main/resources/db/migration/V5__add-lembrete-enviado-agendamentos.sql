ALTER TABLE agendamentos
    ADD COLUMN lembrete_enviado BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_agendamentos_lembrete_enviado ON agendamentos (lembrete_enviado);
