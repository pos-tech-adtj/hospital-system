package com.fiap.api_notificacao.service;

import com.fiap.api_notificacao.domain.Notificacao;
import com.fiap.api_notificacao.domain.TipoUsuario;
import com.fiap.api_notificacao.dto.event.ConsultaEvento;
import com.fiap.api_notificacao.repository.NotificacaoRepository;
import com.fiap.api_notificacao.service.notificacao.NotificacaoSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.fiap.api_notificacao.security.UsuarioPrincipal;

@Service
public class ConsultaNotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(ConsultaNotificacaoService.class);

    private final NotificacaoRepository notificacaoRepository;
    private final NotificacaoSender notificacaoSender;

    public ConsultaNotificacaoService(NotificacaoRepository notificacaoRepository,
                                      NotificacaoSender notificacaoSender) {
        this.notificacaoRepository = notificacaoRepository;
        this.notificacaoSender = notificacaoSender;
    }

    @Transactional
    public void processar(ConsultaEvento evento) {
        if (notificacaoRepository.existsByEventId(evento.eventId())) {
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
            Notificacao notificacao = notificacaoRepository.save(Notificacao.pendente(
                    evento.eventId(), evento.consultaId(), evento.pacienteId(), evento.pacienteNome(),
                    evento.pacienteEmail(), evento.medicoNome(), evento.dataHora().toLocalDateTime(),
                    evento.tipoEvento()));
            try {
                notificacaoSender.enviar(evento);
                notificacao.marcarEnviada(LocalDateTime.now());
            } catch (RuntimeException ex) {
                notificacao.marcarFalha();
                log.error("notificacao_falhou eventId={} consultaId={}", evento.eventId(), evento.consultaId(), ex);
            }
            notificacaoRepository.save(notificacao);
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
            "notificacao_processada eventId={} tipoEvento={} consultaId={} status=sucesso",
                evento.eventId(),
                evento.tipoEvento(),
                evento.consultaId()
        );
    }

    @Transactional(readOnly = true)
    public List<Notificacao> listarPorPaciente(UsuarioPrincipal principal) {
        return notificacaoRepository.findByPacienteIdOrderByDataNotificacaoDesc(principal.getId());
    }

    @Transactional(readOnly = true)
    public List<Notificacao> listarPorConsulta(UUID consultaId, UsuarioPrincipal principal) {
        if (principal.getTipo() == TipoUsuario.PACIENTE) {
                return notificacaoRepository.findByConsultaIdAndPacienteIdOrderByDataNotificacaoDesc(
                    consultaId, principal.getId());
        }
        return notificacaoRepository.findByConsultaIdOrderByDataNotificacaoDesc(consultaId);
    }
}
