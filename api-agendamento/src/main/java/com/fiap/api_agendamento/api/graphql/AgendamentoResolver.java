package com.fiap.api_agendamento.api.graphql;

import com.fiap.api_agendamento.domain.Agendamento;
import com.fiap.api_agendamento.domain.StatusAgendamento;
import com.fiap.api_agendamento.service.AgendamentoService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class AgendamentoResolver {

    private final AgendamentoService agendamentoService;

    public AgendamentoResolver(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Agendamento> agendamentosPorPaciente(@Argument UUID idPaciente) {
        return agendamentoService.listarPorPaciente(idPaciente);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ROLE_ENFERMEIRO')")
    public Agendamento registrarConsulta(
            @Argument UUID pacienteId,
            @Argument UUID medicoId,
            @Argument LocalDateTime dataHora,
            @Argument String especialidade,
            @Argument String observacoes
    ) {
        return agendamentoService.registrarConsulta(pacienteId, medicoId, dataHora, especialidade, observacoes);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ROLE_MEDICO')")
    public Agendamento editarConsulta(
            @Argument UUID idConsulta,
            @Argument LocalDateTime dataHora,
            @Argument StatusAgendamento status,
            @Argument String especialidade,
            @Argument String observacoes
    ) {
        return agendamentoService.editarConsulta(idConsulta, dataHora, status, especialidade, observacoes);
    }
}