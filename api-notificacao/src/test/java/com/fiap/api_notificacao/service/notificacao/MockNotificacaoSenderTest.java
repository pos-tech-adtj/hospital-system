package com.fiap.api_notificacao.service.notificacao;

import com.fiap.api_notificacao.dto.event.ConsultaEvento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@TestPropertySource(properties = {"notificacao.tipo=mock"})
class MockNotificacaoSenderTest {

    @Autowired
    private NotificacaoSender notificacaoSender;

    @Test
    void enviarLembrete_deveProcessarSemErros() {
        ConsultaEvento evento = new ConsultaEvento(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "João Silva",
                "joao@email.com",
                UUID.randomUUID(),
                "Dr. Maria Santos",
                OffsetDateTime.now().plusDays(1),
                "Cardiologia",
                "Paciente com histórico de hipertensão",
                "AGENDADA",
                ConsultaEvento.TIPO_LEMBRETE,
                OffsetDateTime.now()
        );

        assertDoesNotThrow(() -> notificacaoSender.enviarLembrete(evento));
    }
}