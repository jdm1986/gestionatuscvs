package com.example.gestion_curriculums0.repository;

import com.example.gestion_curriculums0.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// Declaro la interfaz UsuarioRepository que extiende JpaRepository para manejar las operaciones CRUD de la entidad Usuario
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Defino un método para buscar un usuario por su nombre de usuario
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    // Defino un método para buscar un usuario por su email
    Optional<Usuario> findByEmail(String email);

    // Defino un método para buscar un usuario por su nombre de usuario o su email
    Optional<Usuario> findByNombreUsuarioOrEmail(String nombreUsuario, String email);

    // Defino un método para buscar un usuario por su token de restablecimiento de contraseña
    Optional<Usuario> findByResetToken(String resetToken);

    // Defino un método para verificar si un usuario existe por su email
    boolean existsByEmail(String email);
}
