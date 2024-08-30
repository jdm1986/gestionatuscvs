package com.example.gestion_curriculums0.repository;

import com.example.gestion_curriculums0.model.UploadLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UploadLinkRepository extends JpaRepository<UploadLink, Long> {
    Optional<UploadLink> findByUploadId(String uploadId);
    void deleteByExpiryDateBefore(LocalDateTime now); // Para limpiar enlaces expirados
}
