package com.fiap.api_agendamento.dto;

import com.fiap.api_agendamento.domain.StatusAgendamento;

import java.time.OffsetDateTime;

public record EditarConsultaInput(
    OffsetDateTime dataHora,
    StatusAgendamento status,
    String especialidade,
    String observacoes
) {
}

