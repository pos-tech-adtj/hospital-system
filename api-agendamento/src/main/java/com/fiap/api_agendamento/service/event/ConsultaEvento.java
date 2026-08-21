package com.fiap.api_agendamento.service.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ConsultaEvento(
        UUID consultaId,
        UUID pacienteId,
        String pacienteNome,
        String pacienteEmail,
        UUID medicoId,
        String medicoNome,
        OffsetDateTime dataHora,
        String especialidade,
        String observacoes,
        String status,
        String tipoEvento,
        OffsetDateTime ocorridoEm
) {

    public static final String TIPO_CRIADA = "consulta.criada";
    public static final String TIPO_ATUALIZADA = "consulta.atualizada";
}