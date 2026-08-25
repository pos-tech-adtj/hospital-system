package com.fiap.api_notificacao.service.event;

import com.fiap.api_notificacao.config.RabbitMqConfig;
import com.fiap.api_notificacao.dto.event.ConsultaEvento;
import com.fiap.api_notificacao.service.notificacao.NotificacaoSender;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = {
        "notificacao.tipo=mock",
        "spring.rabbitmq.host=localhost"
})
class ConsultaEventListenerTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private NotificacaoSender notificacaoSender;

    @Test
    void processarEventoConsulta_comTipoLembrete_deveEnviarNotificacao() {
        ConsultaEvento evento = new ConsultaEvento(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "João Silva",
                "joao@email.com",
                UUID.randomUUID(),
                "Dr. Maria Santos",
                OffsetDateTime.now().plusDays(1),
                "Cardiologia",
                null,
                "AGENDADA",
                ConsultaEvento.TIPO_LEMBRETE,
                OffsetDateTime.now()
        );

        // Publica evento na fila
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.CONSULTAS_EXCHANGE,
                "consulta.lembrete",
                evento
        );

        // Aguarda processamento
        verify(notificacaoSender, timeout(5000).times(1))
                .enviarLembrete(any(ConsultaEvento.class));
    }
}