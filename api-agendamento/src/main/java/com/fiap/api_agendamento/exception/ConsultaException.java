package com.fiap.api_agendamento.exception;

public class ConsultaException extends RuntimeException {

    private final String codigo;

    public ConsultaException(String mensagem, String codigo) {
        super(mensagem);
        this.codigo = codigo;
    }

    public ConsultaException(String mensagem, String codigo, Throwable causa) {
        super(mensagem, causa);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}

