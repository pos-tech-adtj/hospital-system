package com.fiap.api_notificacao.service.notificacao;

import com.fiap.api_notificacao.config.EmailProperties;
import com.fiap.api_notificacao.dto.event.ConsultaEvento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementação de email para notificações.
 * Ativa quando: notificacao.tipo=email e SMTP está configurado.
 */
@Service
@ConditionalOnProperty(name = "notificacao.tipo", havingValue = "email")
public class EmailNotificacaoSender implements NotificacaoSender {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificacaoSender.class);
    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    public EmailNotificacaoSender(JavaMailSender mailSender, EmailProperties emailProperties) {
        this.mailSender = mailSender;
        this.emailProperties = emailProperties;
    }

    @Override
    public void enviarLembrete(ConsultaEvento evento) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailProperties.getFrom());
            message.setTo(evento.pacienteEmail());
            message.setSubject("Lembrete de Consulta - Hospital System");
            message.setText(construirMensagem(evento));

            mailSender.send(message);
            log.info("Email de lembrete enviado com sucesso para: {}", evento.pacienteEmail());
        } catch (Exception e) {
            log.error("Erro ao enviar email para paciente: {} ({})", 
                    evento.pacienteNome(), evento.pacienteEmail(), e);
            throw new NotificacaoException("Falha ao enviar email de lembrete", e);
        }
    }

    private String construirMensagem(ConsultaEvento evento) {
        return String.format(
                "Olá %s,\n\n" +
                "Este é um lembrete de sua consulta marcada.\n\n" +
                "Detalhes da Consulta:\n" +
                "- Médico: %s\n" +
                "- Data e Hora: %s\n" +
                "- Especialidade: %s\n\n" +
                "Por favor, compareça com 10 minutos de antecedência.\n\n" +
                "Atenciosamente,\nHospital System",
                evento.pacienteNome(),
                evento.medicoNome(),
                evento.dataHora(),
                evento.especialidade()
        );
    }
}