package com.fiap.api_notificacao.security;

import com.fiap.api_notificacao.domain.TipoUsuario;
import com.fiap.api_notificacao.domain.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UsuarioPrincipal implements UserDetails {
    private final UUID id;
    private final String email;
    private final String senha;
    private final TipoUsuario tipo;
    private final boolean ativo;

    private UsuarioPrincipal(Usuario usuario) {
        id = usuario.getId();
        email = usuario.getEmail();
        senha = usuario.getSenha();
        tipo = usuario.getTipo();
        ativo = usuario.isAtivo();
    }

    public static UsuarioPrincipal from(Usuario usuario) {
        return new UsuarioPrincipal(usuario);
    }

    public UUID getId() { return id; }
    public TipoUsuario getTipo() { return tipo; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + tipo.name()));
    }
    @Override public String getPassword() { return senha; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return ativo; }
}