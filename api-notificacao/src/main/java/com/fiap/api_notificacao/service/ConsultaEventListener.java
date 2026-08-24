package com.fiap.api_notificacao.service.event;

import com.fiap.api_notificacao.config.RabbitMqConfig;
import com.fiap.api_notificacao.dto.event.ConsultaEvento;
import com.fiap.api_notificacao.service.notificacao.NotificacaoSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener que consome eventos de consulta da fila RabbitMQ
 * e dispara o envio de notificações através do NotificacaoSender.
 */
@Component
public class ConsultaEventListener {

    private static final Logger log = LoggerFactory.getLogger(ConsultaEventListener.class);
    private final NotificacaoSender notificacaoSender;

    public ConsultaEventListener(NotificacaoSender notificacaoSender) {
        this.notificacaoSender = notificacaoSender;
    }

    /**
     * Processa eventos de consulta recebidos da fila de notificação.
     * Filtra apenas eventos do tipo LEMBRETE e envia a notificação.
     */
    @RabbitListener(queues = RabbitMqConfig.NOTIFICACAO_CONSULTA_QUEUE)
    public void processarEventoConsulta(ConsultaEvento evento) {
        try {
            log.debug("Evento recebido: tipo={}, paciente={}, consultaId={}", 
                    evento.tipoEvento(), evento.pacienteNome(), evento.consultaId());

            // Processa apenas lembretes
            if (ConsultaEvento.TIPO_LEMBRETE.equals(evento.tipoEvento())) {
                notificacaoSender.enviarLembrete(evento);
                log.info("Lembrete processado com sucesso para paciente: {} (consultaId={})", 
                        evento.pacienteNome(), evento.consultaId());
            } else {
                log.debug("Evento ignorado (tipo não é lembrete): {}", evento.tipoEvento());
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento de lembrete (consultaId={}): {}", 
                    evento.consultaId(), e.getMessage(), e);
            // A exceção será capturada e a mensagem irá para Dead Letter Queue
            throw e;
        }
    }
}