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
            String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            // Conservar solo el nombre base y saneado
            String baseName = Paths.get(original).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_");
            if (!baseName.toLowerCase().endsWith(".pdf")) {
                baseName = baseName + ".pdf";
            }
            Path target = this.fileStorageLocation.resolve(safeName + "_" + unique + "_" + baseName).normalize();
            if (!target.startsWith(this.fileStorageLocation)) {
                throw new RuntimeException("Ruta de destino inválida");
            }
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toAbsolutePath().toString(); // <<--- guardaremos esto en BD
        } catch (Exception ex) {
            throw new RuntimeException("No pude almacenar el archivo " + file.getOriginalFilename(), ex);
        }
    }

    public Resource loadFile(String filePath) {
        try {
            Path path = Paths.get(filePath).normalize();
            if (!path.startsWith(this.fileStorageLocation)) {
                throw new RuntimeException("Acceso a ruta fuera del almacenamiento no permitido");
            }
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) return resource;
            throw new RuntimeException("Archivo no encontrado o no legible: " + filePath);
        } catch (Exception ex) {
            throw new RuntimeException("Archivo no encontrado: " + filePath, ex);
        }
    }

    public Resource loadFileFromStorage(String filename) {
        try {
            String clean = Paths.get(filename).getFileName().toString();
            Path path = this.fileStorageLocation.resolve(clean).normalize();
            if (!path.startsWith(this.fileStorageLocation)) {
                throw new RuntimeException("Acceso a ruta fuera del almacenamiento no permitido");
            }
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) return resource;
            throw new RuntimeException("Archivo no encontrado o no legible: " + filename);
        } catch (Exception ex) {
            throw new RuntimeException("Archivo no encontrado: " + filename, ex);
        }
    }
}
