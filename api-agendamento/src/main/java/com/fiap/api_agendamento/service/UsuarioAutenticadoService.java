package com.fiap.api_agendamento.service;

import com.fiap.api_agendamento.dto.UsuarioAutenticadoResponse;
import com.fiap.api_agendamento.security.UsuarioPrincipal;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioAutenticadoService {

    public UsuarioPrincipal obterPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UsuarioPrincipal usuarioPrincipal) {
            return usuarioPrincipal;
        }

        throw new AuthenticationCredentialsNotFoundException("Principal autenticado inválido");
    }

    public UsuarioAutenticadoResponse obterUsuarioAutenticado() {
        return toResponse(obterPrincipal());
    }

    public UsuarioAutenticadoResponse obterUsuarioAutenticado(UsuarioPrincipal principal) {
        if (principal == null) {
            throw new AuthenticationCredentialsNotFoundException("Usuário não autenticado");
        }

        return toResponse(principal);
    }

    private UsuarioAutenticadoResponse toResponse(UsuarioPrincipal principal) {
        List<String> authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new UsuarioAutenticadoResponse(
                principal.getId(),
                principal.getNome(),
                principal.getEmail(),
                principal.getTipo(),
                authorities
        );
    }
}
