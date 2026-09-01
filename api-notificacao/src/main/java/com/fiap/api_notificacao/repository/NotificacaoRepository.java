package com.fiap.api_notificacao.repository;

import com.fiap.api_notificacao.domain.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {
	boolean existsByEventId(UUID eventId);

	List<Notificacao> findByPacienteIdOrderByDataNotificacaoDesc(UUID pacienteId);

	List<Notificacao> findByConsultaIdOrderByDataNotificacaoDesc(UUID consultaId);

	List<Notificacao> findByConsultaIdAndPacienteIdOrderByDataNotificacaoDesc(UUID consultaId, UUID pacienteId);
}
