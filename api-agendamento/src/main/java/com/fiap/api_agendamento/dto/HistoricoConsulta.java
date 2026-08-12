package com.fiap.api_agendamento.dto;

import com.fiap.api_agendamento.domain.Agendamento;
import com.fiap.api_agendamento.domain.StatusAgendamento;

import java.time.OffsetDateTime;
import java.util.UUID;

public record HistoricoConsulta(
        UUID id,
        UUID medicoId,
        UUID pacienteId,
        OffsetDateTime dataHora,
        StatusAgendamento status,
        String especialidade,
        String observacoes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static HistoricoConsulta from(Agendamento agendamento) {
        return new HistoricoConsulta(
                agendamento.getId(),
                agendamento.getMedicoId(),
                agendamento.getPacienteId(),
                agendamento.getDataHora(),
                agendamento.getStatus(),
                agendamento.getEspecialidade(),
                agendamento.getObservacoes(),
                agendamento.getCreatedAt(),
                agendamento.getUpdatedAt()
        );
    }
}
