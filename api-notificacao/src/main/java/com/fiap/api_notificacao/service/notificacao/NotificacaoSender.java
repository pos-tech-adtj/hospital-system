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
    void enviar(ConsultaEvento evento);

    default String construirMensagem(ConsultaEvento evento) {
        String introducao = ConsultaEvento.TIPO_ATUALIZADA.equals(evento.tipoEvento())
                ? "Sua consulta foi atualizada."
                : "Sua consulta foi criada com sucesso.";
        return String.format("Olá %s,\n\n%s\n\nMédico: %s\nData e hora: %s\nEspecialidade: %s",
                evento.pacienteNome(), introducao, evento.medicoNome(), evento.dataHora(), evento.especialidade());
    }
}