package com.fiap.api_agendamento.exception;

import java.util.UUID;

public class ConsultaNaoEncontradaException extends ConsultaException {

    public static final String CODIGO = "CONSULTA_NAO_ENCONTRADA";

    public ConsultaNaoEncontradaException(UUID consultaId) {
        super(
                String.format("Consulta com ID %s não encontrada", consultaId),
                CODIGO
        );
    }

    public ConsultaNaoEncontradaException(String mensagem) {
        super(mensagem, CODIGO);
    }
}

