-- Seed de usuários para testes manuais/collections. Senha em texto puro indicada
-- no comentário de cada linha, sempre "<perfil>123"; nunca gravada em claro no banco.

INSERT INTO usuarios (id, nome, email, cpf, senha, data_nascimento, tipo, ativo)
VALUES
    -- senha: medico123
    ('a0000000-0000-0000-0000-000000000001', 'Dra. Ana Ferreira', 'medico1@hospital.com', '11111111111',
     '$2a$10$RSjCuV13ywArbURzn2RX1.kaxwtnNcEDzltszfK/gk2xNOE3HpkyC', '1980-04-12', 'MEDICO', TRUE),

    -- senha: enfermeiro123
    ('a0000000-0000-0000-0000-000000000002', 'Bruno Carvalho', 'enfermeiro1@hospital.com', '22222222222',
     '$2a$10$LVbOPSKJNY23uj91eHa0SeIgfD16LCKhZrL2sLJder73Kw2l83z1i', '1990-07-25', 'ENFERMEIRO', TRUE),

    -- senha: paciente123
    ('a0000000-0000-0000-0000-000000000003', 'Carlos Souza', 'paciente1@hospital.com', '33333333333',
     '$2a$10$yqsj0k135OSC.JCfsA4TlOSrN7OVsv9wW020Y5tGP4CducF3Bp.y2', '1995-01-10', 'PACIENTE', TRUE),

    -- senha: paciente123
    ('a0000000-0000-0000-0000-000000000004', 'Daniela Lima', 'paciente2@hospital.com', '44444444444',
     '$2a$10$yqsj0k135OSC.JCfsA4TlOSrN7OVsv9wW020Y5tGP4CducF3Bp.y2', '1988-11-30', 'PACIENTE', TRUE),

    -- senha: paciente123
    ('a0000000-0000-0000-0000-000000000005', 'Eduardo Martins', 'paciente3@hospital.com', '55555555555',
     '$2a$10$yqsj0k135OSC.JCfsA4TlOSrN7OVsv9wW020Y5tGP4CducF3Bp.y2', '2001-06-18', 'PACIENTE', TRUE);
