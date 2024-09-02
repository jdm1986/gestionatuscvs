package com.example.gestion_curriculums0.repository;

import com.example.gestion_curriculums0.model.UploadLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadLinkRepository extends JpaRepository<UploadLink, Long> {
    Optional<UploadLink> findByToken(String token);
    // Método para eliminar todos los UploadLink de un usuario específico
    void deleteByUsuarioId(Long usuarioId);
}
