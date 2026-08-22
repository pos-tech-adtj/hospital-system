package com.fiap.api_agendamento.repository;

import com.fiap.api_agendamento.domain.Agendamento;
import com.fiap.api_agendamento.domain.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoRepository
        extends JpaRepository<Agendamento, UUID>, JpaSpecificationExecutor<Agendamento> {

        List<Agendamento> findByPacienteIdOrderByDataHoraDesc(UUID pacienteId);

        Optional<Agendamento> findByMedicoIdAndDataHoraAndStatus(UUID medicoId, OffsetDateTime dataHora, StatusAgendamento status);

        List<Agendamento> findByStatusAndLembreteEnviadoFalseAndDataHoraBetween(
                StatusAgendamento status, OffsetDateTime inicio, OffsetDateTime fim);
}
