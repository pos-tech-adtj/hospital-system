package com.fiap.api_agendamento.service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ConsultaEventoListener {

    private static final Logger log = LoggerFactory.getLogger(ConsultaEventoListener.class);

    private final ConsultaEventPublisher consultaEventPublisher;

    public ConsultaEventoListener(ConsultaEventPublisher consultaEventPublisher) {
        this.consultaEventPublisher = consultaEventPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoConfirmarTransacao(ConsultaEvento evento) {
        try {
            consultaEventPublisher.publicar(evento);
        } catch (Exception e) {
            log.error(
                    "Falha ao publicar evento de consulta no RabbitMQ (eventId={}, tipoEvento={}, consultaId={})",
                    evento.eventId(), evento.tipoEvento(), evento.consultaId(), e
            );
        }
    }
}
