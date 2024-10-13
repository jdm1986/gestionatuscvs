package com.example.gestion_curriculums0.security;

import com.example.gestion_curriculums0.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.Arrays;

// Esta clase implementa UserDetails, que es necesaria para la autenticación en Spring Security
public class CustomUserDetails implements UserDetails {

    private Usuario usuario; // Guardo la referencia al usuario

    // Constructor para inicializar el usuario
    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    // Implemento el método para obtener los roles (authorities) del usuario
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Divido los roles por comas y los convierto en una colección de SimpleGrantedAuthority
        return Arrays.stream(usuario.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // Devuelvo la contraseña del usuario
    @Override
    public String getPassword() {
        return usuario.getContrasena();
    }

    // Devuelvo el nombre de usuario
    @Override
    public String getUsername() {
        return usuario.getNombreUsuario();
    }

    // Indico que la cuenta no ha expirado
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Indico que la cuenta no está bloqueada
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Indico que las credenciales no han expirado
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Indico que la cuenta está habilitada
    @Override
    public boolean isEnabled() {
        return true;
    }
}
