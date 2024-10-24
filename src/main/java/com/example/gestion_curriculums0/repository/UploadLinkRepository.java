// UploadLinkRepository.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.repository;

// Declaro la interfaz UploadLinkRepository que extiende JpaRepository para manejar las operaciones CRUD de la entidad UploadLink

import com.example.gestion_curriculums0.model.UploadLink;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UploadLinkRepository extends JpaRepository<UploadLink, Long> {

    // Defino un método para buscar un enlace de subida (UploadLink) por su token
    Optional<UploadLink> findByToken(String token);

    // Defino un método para eliminar todos los enlaces de subida (UploadLink) asociados a un usuario por su ID
    void deleteByUsuarioId(Long usuarioId);
}
