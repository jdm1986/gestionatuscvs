package com.example.gestion_curriculums0.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/*
  Clase ArchivoService: Me encargo de gestionar la subida y descarga de archivos.
  Guardo los archivos subidos en un directorio local y también puedo cargar un archivo cuando es solicitado.
 */
@Service
public class ArchivoService {

    // Defino la ubicación donde voy a guardar los archivos subidos
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    // Constructor: Me aseguro de que el directorio donde voy a almacenar los archivos exista
    public ArchivoService() {
        try {
            Files.createDirectories(this.fileStorageLocation);  // Creo el directorio si no existe
        } catch (Exception ex) {
            throw new RuntimeException("No pude crear el directorio donde se almacenarán los archivos subidos.", ex);
        }
    }

    // Método para guardar un archivo. Le doy un nombre único basado en el nombre de usuario.
    public String saveFile(MultipartFile file, String username) {
        try {
            // Guardo el archivo con el nombre del usuario para evitar colisiones de nombres
            Path targetLocation = this.fileStorageLocation.resolve(username + "_" + file.getOriginalFilename());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);  // Sobrescribo si ya existe
            return targetLocation.getFileName().toString();  // Retorno el nombre del archivo guardado
        } catch (Exception ex) {
            throw new RuntimeException("No pude almacenar el archivo " + file.getOriginalFilename() + ". ¡Por favor, inténtalo de nuevo!", ex);
        }
    }

    // Método para cargar un archivo. Lo busco por su nombre.
    public Resource loadFile(String fileName) {
        try {
            // Localizo el archivo en el sistema de archivos
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // Verifico si el archivo existe y es legible antes de devolverlo
            if (resource.exists() && resource.isReadable()) {
                return resource;  // Retorno el archivo como recurso
            } else {
                throw new RuntimeException("Archivo no encontrado o no legible: " + fileName);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Archivo no encontrado: " + fileName, ex);
        }
    }
}
