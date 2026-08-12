package com.fiap.api_agendamento.exception;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ConsultaHorarioIndisponiveException extends ConsultaException {

    public static final String CODIGO = "CONSULTA_HORARIO_INDISPONIVEL";

    public ConsultaHorarioIndisponiveException(UUID medicoId, OffsetDateTime dataHora) {
        super(
                String.format(
                        "O médico (ID: %s) já possui uma consulta agendada em %s",
                        medicoId,
                        dataHora
                ),
                CODIGO
        );
    }

    public ConsultaHorarioIndisponiveException(String mensagem) {
        super(mensagem, CODIGO);
    }
}

