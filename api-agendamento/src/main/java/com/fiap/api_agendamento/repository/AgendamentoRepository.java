package com.fiap.api_agendamento.repository;

import com.fiap.api_agendamento.domain.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AgendamentoRepository
        extends JpaRepository<Agendamento, UUID> {

        List<Agendamento> findAllByOrderByDataHoraDesc();

        List<Agendamento> findByPacienteIdOrderByDataHoraDesc(UUID pacienteId);

}
