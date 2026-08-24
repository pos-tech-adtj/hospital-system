package com.fiap.api_notificacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "eventos_processados")
public class EventoProcessado {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "processado_em", nullable = false)
    private OffsetDateTime processadoEm;

    protected EventoProcessado() {
    }

    public EventoProcessado(UUID eventId, OffsetDateTime processadoEm) {
        this.eventId = eventId;
        this.processadoEm = processadoEm;
    }

    public UUID getEventId() {
        return eventId;
    }

    public OffsetDateTime getProcessadoEm() {
        return processadoEm;
    }
}
