package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import com.example.gestion_curriculums0.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Optional<Usuario> usuario = usuarioRepository.findByNombreUsuarioOrEmail(usernameOrEmail, usernameOrEmail);
        if (usuario.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        return new CustomUserDetails(usuario.get());
    }

    public Long getUserIdByUsername(String usernameOrEmail) {
        Optional<Usuario> usuario = usuarioRepository.findByNombreUsuarioOrEmail(usernameOrEmail, usernameOrEmail);
        if (usuario.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        return usuario.get().getId();
    }

    public void saveUser(AuthRequest authRequest) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(authRequest.getUsername());
        usuario.setContrasena(passwordEncoder.encode(authRequest.getPassword()));
        usuario.setEmail(authRequest.getEmail());
        usuario.setRoles(authRequest.getRoles() != null ? authRequest.getRoles() : "USER");
        usuarioRepository.save(usuario);
    }

    // Método para verificar si un usuario existe en la base de datos
    public boolean userExists(String usernameOrEmail) {
        return usuarioRepository.findByNombreUsuarioOrEmail(usernameOrEmail, usernameOrEmail).isPresent();
    }
}
