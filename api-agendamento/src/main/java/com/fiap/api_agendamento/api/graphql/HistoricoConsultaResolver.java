package com.fiap.api_agendamento.api.graphql;

import com.fiap.api_agendamento.dto.HistoricoConsulta;
import com.fiap.api_agendamento.dto.HistoricoConsultaFiltro;
import com.fiap.api_agendamento.service.AgendamentoService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class HistoricoConsultaResolver {

    private final AgendamentoService agendamentoService;

    public HistoricoConsultaResolver(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<HistoricoConsulta> historicoConsultas(@Argument("filter") HistoricoConsultaFiltro filter) {
        return agendamentoService.listarHistoricoConsultas(filter).stream()
                .map(HistoricoConsulta::from)
                .toList();
    }
}
