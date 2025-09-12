package com.example.gestion_curriculums0.service;

import org.springframework.beans.factory.annotation.Value;          // <--- IMPORT CLAVE
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;                          // <--- IMPORT CLAVE

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ArchivoService {

    @Value("${app.storage-dir:uploads}")
    private String storageDir;

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        try {
            this.fileStorageLocation = Paths.get(storageDir).toAbsolutePath().normalize();
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No pude crear el directorio de uploads: " + storageDir, ex);
        }
    }

    public String saveFile(MultipartFile file, String username) {
        try {
            String safeName = username.replaceAll("[^a-zA-Z0-9._-]", "_");
            String unique = String.valueOf(System.currentTimeMillis());
            Path target = this.fileStorageLocation.resolve(safeName + "_" + unique + "_" + file.getOriginalFilename());
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toAbsolutePath().toString(); // <<--- guardaremos esto en BD
        } catch (Exception ex) {
            throw new RuntimeException("No pude almacenar el archivo " + file.getOriginalFilename(), ex);
        }
    }

    public Resource loadFile(String filePath) {
        try {
            Path path = Paths.get(filePath).normalize();
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) return resource;
            throw new RuntimeException("Archivo no encontrado o no legible: " + filePath);
        } catch (Exception ex) {
            throw new RuntimeException("Archivo no encontrado: " + filePath, ex);
        }
    }
}
