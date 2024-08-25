package com.example.gestion_curriculums0.repository;

import com.example.gestion_curriculums0.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNombreUsuarioOrEmail(String nombreUsuario, String email);
    Optional<Usuario> findByResetToken(String resetToken);

    // Añadimos el método para verificar la existencia del usuario por email
    boolean existsByEmail(String email);
}
