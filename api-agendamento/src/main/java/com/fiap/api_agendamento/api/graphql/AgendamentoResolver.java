package com.fiap.api_agendamento.api.graphql;

import com.fiap.api_agendamento.domain.Agendamento;
import com.fiap.api_agendamento.service.AgendamentoService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

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
}