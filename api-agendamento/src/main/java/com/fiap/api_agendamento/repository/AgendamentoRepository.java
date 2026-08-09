package com.fiap.api_agendamento.repository;

import com.fiap.api_agendamento.domain.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoRepository
        extends JpaRepository<Agendamento, Long> {

}
