package com.fiap.api_notificacao.service.notificacao;

import com.fiap.api_notificacao.dto.event.ConsultaEvento;

/**
 * Interface para envio de notificações de lembretes de consulta.
 * Permite múltiplas implementações (log/mock, email, SMS, etc).
 */
public interface NotificacaoSender {
    
    /**
     * Envia notificação de lembrete de consulta para o paciente.
     * 
     * @param evento dados da consulta para criar a notificação
     */
    void enviarLembrete(ConsultaEvento evento);
}