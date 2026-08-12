package com.fiap.api_agendamento.repository;

import com.fiap.api_agendamento.domain.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoRepository
        extends JpaRepository<Agendamento, UUID>, JpaSpecificationExecutor<Agendamento> {

        List<Agendamento> findByPacienteIdOrderByDataHoraDesc(UUID pacienteId);

        Optional<Agendamento> findByMedicoIdAndDataHora(UUID medicoId, OffsetDateTime dataHora);
}
