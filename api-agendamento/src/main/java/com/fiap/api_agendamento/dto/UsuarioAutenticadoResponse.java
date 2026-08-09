package com.fiap.api_agendamento.dto;

import com.fiap.api_agendamento.domain.TipoUsuario;

import java.util.List;
import java.util.UUID;

public record UsuarioAutenticadoResponse(
        UUID id,
        String nome,
        String email,
        TipoUsuario tipo,
        List<String> authorities
) {
}
