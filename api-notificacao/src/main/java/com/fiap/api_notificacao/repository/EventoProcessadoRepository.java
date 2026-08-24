package com.fiap.api_notificacao.repository;

import com.fiap.api_notificacao.domain.EventoProcessado;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoProcessadoRepository extends JpaRepository<EventoProcessado, UUID> {
}
