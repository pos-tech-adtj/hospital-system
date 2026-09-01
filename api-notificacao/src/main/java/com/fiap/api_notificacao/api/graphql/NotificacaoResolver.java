package com.fiap.api_notificacao.api.graphql;

import com.fiap.api_notificacao.domain.Notificacao;
import com.fiap.api_notificacao.security.UsuarioPrincipal;
import com.fiap.api_notificacao.service.ConsultaNotificacaoService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class NotificacaoResolver {
    private final ConsultaNotificacaoService service;

    public NotificacaoResolver(ConsultaNotificacaoService service) {
        this.service = service;
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Notificacao> notificacoesPorPaciente(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return service.listarPorPaciente(principal);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Notificacao> notificacoesPorConsulta(
            @Argument UUID idConsulta,
            @AuthenticationPrincipal UsuarioPrincipal principal) {
        return service.listarPorConsulta(idConsulta, principal);
    }
}