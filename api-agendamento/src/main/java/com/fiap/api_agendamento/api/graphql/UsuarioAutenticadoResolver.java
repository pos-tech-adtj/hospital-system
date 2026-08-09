package com.fiap.api_agendamento.api.graphql;

import com.fiap.api_agendamento.dto.UsuarioAutenticadoResponse;
import com.fiap.api_agendamento.security.UsuarioPrincipal;
import com.fiap.api_agendamento.service.UsuarioAutenticadoService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
public class UsuarioAutenticadoResolver {

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public UsuarioAutenticadoResolver(UsuarioAutenticadoService usuarioAutenticadoService) {
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @QueryMapping
    public UsuarioAutenticadoResponse usuarioAutenticado(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return usuarioAutenticadoService.obterUsuarioAutenticado(principal);
    }
}
