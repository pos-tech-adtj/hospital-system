package com.fiap.api_agendamento.service.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ConsultaEvento(
        UUID eventId,
        String tipoEvento,
        UUID consultaId,
        UUID pacienteId,
        String pacienteNome,
        String pacienteEmail,
        UUID medicoId,
        String medicoNome,
        String medicoEmail,
        OffsetDateTime dataHora,
        String especialidade,
        String observacoes,
        String status,
        OffsetDateTime timestamp
) {

    public static final String TIPO_CRIADA = "consulta.criada";
    public static final String TIPO_ATUALIZADA = "consulta.atualizada";
}