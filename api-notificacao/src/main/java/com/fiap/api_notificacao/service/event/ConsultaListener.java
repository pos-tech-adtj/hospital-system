package com.fiap.api_notificacao.service.event;

import com.fiap.api_notificacao.config.RabbitMqConfig;
import com.fiap.api_notificacao.dto.event.ConsultaEvento;
import com.fiap.api_notificacao.service.ConsultaNotificacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ConsultaListener {

    private static final Logger log = LoggerFactory.getLogger(ConsultaListener.class);

    private final ConsultaNotificacaoService consultaNotificacaoService;

    public ConsultaListener(ConsultaNotificacaoService consultaNotificacaoService) {
        this.consultaNotificacaoService = consultaNotificacaoService;
    }

    @RabbitListener(queues = RabbitMqConfig.NOTIFICACAO_CONSULTA_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumir(ConsultaEvento evento) {
        try {
            log.info(
                    "evento_recebido eventId={} tipoEvento={} consultaId={} pacienteEmail={} medicoId={} dataHora={}",
                    evento.eventId(),
                    evento.tipoEvento(),
                    evento.consultaId(),
                    evento.pacienteEmail(),
                    evento.medicoId(),
                    evento.dataHora()
            );
            if (ConsultaEvento.TIPO_CRIADA.equals(evento.tipoEvento())
                    || ConsultaEvento.TIPO_ATUALIZADA.equals(evento.tipoEvento())
                    || ConsultaEvento.TIPO_LEMBRETE.equals(evento.tipoEvento())) {
                consultaNotificacaoService.processar(evento);
            }
        } catch (Exception e) {
            log.error(
                    "evento_falhou eventId={} tipoEvento={} consultaId={} status=erro",
                    evento.eventId(),
                    evento.tipoEvento(),
                    evento.consultaId(),
                    e
            );
            throw e;
        }
    }
}
