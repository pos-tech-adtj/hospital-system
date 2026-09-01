package com.fiap.api_notificacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacoes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(of = "id")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "consulta_id", nullable = false, updatable = false)
    private UUID consultaId;

    @Column(name = "paciente_id", nullable = false, updatable = false)
    private UUID pacienteId;

    @Column(name = "paciente_nome", nullable = false, updatable = false)
    private String pacienteNome;

    @Column(name = "paciente_email", nullable = false, updatable = false)
    private String pacienteEmail;

    @Column(name = "medico_nome")
    private String medicoNome;

    @Column(name = "data_consulta", nullable = false)
    private LocalDateTime dataConsulta;

    private String motivo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "status_enum")
    private StatusNotificacao status;

    @Column(name = "data_notificacao")
    private LocalDateTime dataNotificacao;

    @Column(name = "tentativas_envio", nullable = false)
    private int tentativasEnvio;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public static Notificacao pendente(UUID eventId, UUID consultaId, UUID pacienteId, String pacienteNome,
                                       String pacienteEmail, String medicoNome, LocalDateTime dataConsulta,
                           String motivo) {
        return Notificacao.builder()
                .eventId(eventId)
                .consultaId(consultaId)
                .pacienteId(pacienteId)
                .pacienteNome(pacienteNome)
                .pacienteEmail(pacienteEmail)
                .medicoNome(medicoNome)
                .dataConsulta(dataConsulta)
                .motivo(motivo)
                .status(StatusNotificacao.PENDENTE)
                .build();
    }

    public void marcarEnviada(LocalDateTime dataNotificacao) {
        this.status = StatusNotificacao.ENVIADA;
        this.tentativasEnvio++;
        this.dataNotificacao = dataNotificacao;
    }

    public void marcarFalha() {
        this.status = StatusNotificacao.FALHA;
        this.tentativasEnvio++;
    }

    public String getDataConsulta() {
        return dataConsulta == null ? null : dataConsulta.toString();
    }

    public String getDataNotificacao() {
        return dataNotificacao == null ? null : dataNotificacao.toString();
    }

    public String getCreatedAt() {
        return createdAt == null ? null : createdAt.toString();
    }

    public String getUpdatedAt() {
        return updatedAt == null ? null : updatedAt.toString();
    }
}
