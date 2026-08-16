package com.fiap.api_agendamento.exception;

import com.fiap.api_agendamento.domain.StatusAgendamento;

import java.util.UUID;

public class ConsultaNaoAgendadaException extends ConsultaException {

  public static final String CODIGO = "CONSULTA_NAO_AGENDADA";

  public ConsultaNaoAgendadaException(UUID consultaId, StatusAgendamento status) {
    super(
            String.format(
                    "A consulta %s não pode ser alterada ou cancelada porque está com status %s. " +
                            "Somente consultas com status = AGENDADA podem ser alteradas ou canceladas",
                    consultaId,
                    status
            ),
            CODIGO
    );
  }
}