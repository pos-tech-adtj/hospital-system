package com.fiap.api_agendamento.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsultaEventoListenerTest {

    @Mock
    private ConsultaEventPublisher consultaEventPublisher;

    @InjectMocks
    private ConsultaEventoListener consultaEventoListener;

    private ConsultaEvento novoEvento() {
        return new ConsultaEvento(
                UUID.randomUUID(),
                ConsultaEvento.TIPO_CRIADA,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Maria Paciente",
                "maria@teste.com",
                UUID.randomUUID(),
                "Dr. João",
                "joao@teste.com",
                OffsetDateTime.now().plusDays(1),
                "Cardiologia",
                null,
                "AGENDADA",
                OffsetDateTime.now()
        );
    }

    @Test
    void aoConfirmarTransacao_devePublicarEventoNoRabbit() {
        ConsultaEvento evento = novoEvento();

        consultaEventoListener.aoConfirmarTransacao(evento);

        verify(consultaEventPublisher).publicar(evento);
    }

    @Test
    void aoConfirmarTransacao_quandoPublicacaoFalha_naoDevePropagarExcecao() {
        ConsultaEvento evento = novoEvento();
        doThrow(new RuntimeException("broker indisponível")).when(consultaEventPublisher).publicar(evento);

        assertThatCode(() -> consultaEventoListener.aoConfirmarTransacao(evento))
                .doesNotThrowAnyException();
    }
}
