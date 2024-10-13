/* Esta clase gestiona la lógica relacionada con los enlaces de subida de archivos (UploadLink).
   Se encarga de generar, validar y gestionar el contador de subidas asociado a cada enlace.*/

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

    // Genero un nuevo enlace de subida con un token único y fecha de expiración de 5 días
    public UploadLink generateUploadLink() {
        UploadLink uploadLink = new UploadLink();
        uploadLink.setToken(UUID.randomUUID().toString()); // Genero un token único para identificar el enlace
        uploadLink.setExpirationDate(LocalDateTime.now().plusDays(5)); // El enlace caducará en 5 días
        return uploadLinkRepository.save(uploadLink); // Guardo el enlace en la base de datos y lo retorno
    }

    // Busco un enlace de subida a partir de su token
    public Optional<UploadLink> findByToken(String token) {
        return uploadLinkRepository.findByToken(token); // Retorno el enlace correspondiente al token
    }

    // Verifico si el enlace es válido: no ha caducado y no ha alcanzado el límite de subidas
    public boolean isLinkValid(UploadLink uploadLink) {
        // El enlace es válido si no ha caducado y no se ha alcanzado el límite de subidas
        return uploadLink.getExpirationDate().isAfter(LocalDateTime.now()) &&
                uploadLink.getUploadCount() < uploadLink.getUploadLimit();
    }

    // Incremento el contador de subidas del enlace cada vez que se sube un archivo con ese enlace
    public void incrementUploadCount(UploadLink uploadLink) {
        uploadLink.setUploadCount(uploadLink.getUploadCount() + 1); // Incremento el contador
        uploadLinkRepository.save(uploadLink); // Guardo los cambios en la base de datos
    }
}
