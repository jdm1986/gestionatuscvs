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

/*
  Clase CustomUserDetailsService: Responsable de cargar los detalles del usuario desde la base de datos
  según su nombre de usuario o email, e interactuar con el sistema de autenticación de Spring Security.
  También proporciono métodos adicionales para verificar si un usuario existe y guardar nuevos usuarios.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository; // Inyecto el repositorio de usuarios

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyecto el codificador de contraseñas

    /*
      Este método es llamado por Spring Security para cargar un usuario por su nombre de usuario o email.
      Si el usuario no se encuentra, lanzo una excepción.
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Optional<Usuario> usuario = usuarioRepository.findByNombreUsuarioOrEmail(usernameOrEmail, usernameOrEmail);
        if (usuario.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        return new CustomUserDetails(usuario.get()); // Devuelvo los detalles del usuario encontrado
    }

    //Devuelvo el ID del usuario a partir de su nombre de usuario o email.

    public Long getUserIdByUsername(String usernameOrEmail) {
        Optional<Usuario> usuario = usuarioRepository.findByNombreUsuarioOrEmail(usernameOrEmail, usernameOrEmail);
        if (usuario.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        return usuario.get().getId();
    }

    //Guardo un nuevo usuario en la base de datos con los detalles proporcionados.

    public void saveUser(AuthRequest authRequest) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(authRequest.getUsername()); // Establezco el nombre de usuario
        usuario.setContrasena(passwordEncoder.encode(authRequest.getPassword())); // Codifico y guardo la contraseña
        usuario.setEmail(authRequest.getEmail()); // Establezco el email
        usuario.setRoles(authRequest.getRoles() != null ? authRequest.getRoles() : "USER"); // Asigno el rol por defecto si no se especifica
        usuarioRepository.save(usuario); // Guardo el usuario en la base de datos
    }

    //Verifico si un usuario existe en la base de datos por su nombre de usuario o email.

    public boolean userExists(String usernameOrEmail) {
        return usuarioRepository.findByNombreUsuarioOrEmail(usernameOrEmail, usernameOrEmail).isPresent();
    }

    //Verifico si un email ya está registrado en la base de datos.

    public boolean userExistsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
