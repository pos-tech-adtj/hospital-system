package com.fiap.api_agendamento.exception;

import java.time.OffsetDateTime;

public class ConsultaDataPassadaException extends ConsultaException {

    public static final String CODIGO = "CONSULTA_DATA_PASSADA";

    public ConsultaDataPassadaException(OffsetDateTime dataHora) {
        super(
                String.format("A data e hora da consulta (%s) não pode ser no passado", dataHora),
                CODIGO
        );
    }

    public ConsultaDataPassadaException(String mensagem) {
        super(mensagem, CODIGO);
    }
}

