package com.fiap.api_agendamento.dto;

import com.fiap.api_agendamento.domain.StatusAgendamento;

public record HistoricoConsultaFiltro(
        StatusAgendamento status,
        Boolean apenasFuturas
) {
}
