package com.fiap.api_notificacao.service;

import com.fiap.api_notificacao.domain.EventoProcessado;
import com.fiap.api_notificacao.dto.event.ConsultaEvento;
import com.fiap.api_notificacao.repository.EventoProcessadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.OffsetDateTime;

@Service
public class ConsultaNotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(ConsultaNotificacaoService.class);

    private final EventoProcessadoRepository eventoProcessadoRepository;

    public ConsultaNotificacaoService(EventoProcessadoRepository eventoProcessadoRepository) {
        this.eventoProcessadoRepository = eventoProcessadoRepository;
    }

    @Transactional
    public void processar(ConsultaEvento evento) {
        if (eventoProcessadoRepository.existsById(evento.eventId())) {
            log.info(
                    "evento_duplicado eventId={} tipoEvento={} consultaId={} status=ignorado",
                    evento.eventId(),
                    evento.tipoEvento(),
                    evento.consultaId()
            );
            return;
        }

        log.info(
                "evento_recebido eventId={} tipoEvento={} consultaId={} pacienteEmail={} dataHora={} status={}",
                evento.eventId(),
                evento.tipoEvento(),
                evento.consultaId(),
                evento.pacienteEmail(),
                evento.dataHora(),
                evento.status()
        );

        try {
            eventoProcessadoRepository.save(new EventoProcessado(evento.eventId(), OffsetDateTime.now()));
        } catch (DataIntegrityViolationException ex) {
            log.info(
                    "evento_duplicado_concorrente eventId={} tipoEvento={} consultaId={} status=ignorado",
                    evento.eventId(),
                    evento.tipoEvento(),
                    evento.consultaId()
            );
            return;
        }

        log.info(
                "evento_processado eventId={} tipoEvento={} consultaId={} status=sucesso",
                evento.eventId(),
                evento.tipoEvento(),
                evento.consultaId()
        );
    }
}
