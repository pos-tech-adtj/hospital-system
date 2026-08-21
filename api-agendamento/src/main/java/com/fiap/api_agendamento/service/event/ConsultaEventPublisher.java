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

    public void publicarConsultaCriada(ConsultaEvento evento) {
        publicar(ConsultaEvento.TIPO_CRIADA, evento);
    }

    public void publicarConsultaAtualizada(ConsultaEvento evento) {
        publicar(ConsultaEvento.TIPO_ATUALIZADA, evento);
    }

    private void publicar(String routingKey, ConsultaEvento evento) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.CONSULTAS_EXCHANGE, routingKey, evento);
    }
}