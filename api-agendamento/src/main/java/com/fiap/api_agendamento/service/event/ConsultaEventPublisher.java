package com.fiap.api_agendamento.service.event;

import com.fiap.api_agendamento.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConsultaEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ConsultaEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicar(ConsultaEvento evento) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.CONSULTAS_EXCHANGE, evento.tipoEvento(), evento);
    }
}
