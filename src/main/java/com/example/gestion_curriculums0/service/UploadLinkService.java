package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.UploadLink;
import com.example.gestion_curriculums0.repository.UploadLinkRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UploadLinkService {

    private final UploadLinkRepository uploadLinkRepository;

    public UploadLinkService(UploadLinkRepository uploadLinkRepository) {
        this.uploadLinkRepository = uploadLinkRepository;
    }

    // Crear un nuevo enlace de subida con un token único
    public UploadLink generateUploadLink() {
        UploadLink uploadLink = new UploadLink();
        uploadLink.setToken(UUID.randomUUID().toString()); // Generar un token único
        uploadLink.setExpirationDate(LocalDateTime.now().plusDays(5)); // Caduca en 5 días
        return uploadLinkRepository.save(uploadLink);
    }

    // Obtener un enlace por su token
    public Optional<UploadLink> findByToken(String token) {
        return uploadLinkRepository.findByToken(token);
    }

    // Verificar si el enlace es válido
    public boolean isLinkValid(UploadLink uploadLink) {
        return uploadLink.getExpirationDate().isAfter(LocalDateTime.now()) &&
                uploadLink.getUploadCount() < uploadLink.getUploadLimit();
    }

    // Incrementar el contador de subidas
    public void incrementUploadCount(UploadLink uploadLink) {
        uploadLink.setUploadCount(uploadLink.getUploadCount() + 1);
        uploadLinkRepository.save(uploadLink);
    }
}
