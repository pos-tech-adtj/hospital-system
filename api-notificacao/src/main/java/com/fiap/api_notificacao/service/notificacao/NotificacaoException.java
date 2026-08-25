package com.fiap.api_notificacao.service.notificacao;

public class NotificacaoException extends RuntimeException {
    
    public NotificacaoException(String message) {
        super(message);
    }

    public NotificacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}