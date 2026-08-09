package com.fiap.api_agendamento.security;

import com.fiap.api_agendamento.domain.TipoUsuario;
import com.fiap.api_agendamento.domain.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UsuarioPrincipal implements UserDetails {

    private final UUID id;
    private final String nome;
    private final String email;
    private final String senha;
    private final TipoUsuario tipo;
    private final boolean ativo;
    private final List<GrantedAuthority> authorities;

    private UsuarioPrincipal(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        this.tipo = usuario.getTipo();
        this.ativo = usuario.isAtivo();
        this.authorities = List.of(new SimpleGrantedAuthority(roleName(usuario.getTipo())));
    }

    public static UsuarioPrincipal from(Usuario usuario) {
        return new UsuarioPrincipal(usuario);
    }

    public static String roleName(TipoUsuario tipo) {
        return "ROLE_" + tipo.name();
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}
