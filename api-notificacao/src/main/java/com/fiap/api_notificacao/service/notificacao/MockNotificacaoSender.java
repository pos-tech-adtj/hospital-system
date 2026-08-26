package com.fiap.api_notificacao.service.notificacao;

import com.fiap.api_notificacao.dto.event.ConsultaEvento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Implementação de mock/log para notificações.
 * Ativa quando: notificacao.tipo=mock ou quando SMTP não está configurado.
 */
@Service
@ConditionalOnProperty(name = "notificacao.tipo", havingValue = "mock", matchIfMissing = true)
public class MockNotificacaoSender implements NotificacaoSender {

    private static final Logger log = LoggerFactory.getLogger(MockNotificacaoSender.class);

    @Override
    public void enviar(ConsultaEvento evento) {
        String mensagem = construirMensagem(evento);
        log.info("[MOCK NOTIFICAÇÃO] Lembrete enviado para paciente: {}\n{}", 
                evento.pacienteNome(), mensagem);
    }

}