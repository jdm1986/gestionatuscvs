// ArchivoController.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.controller;

// Este controlador gestiona la subida y descarga de archivos en mi aplicación

import com.example.gestion_curriculums0.service.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;

// Con la anotación @RestController, indico que este controlador está asociado a la ruta "/api/files"
// y maneja las operaciones relacionadas con archivos
@RestController
@RequestMapping("/api/files")
public class ArchivoController {

    // Inyecto el servicio de archivos para manejar la lógica de subida y descarga
    @Autowired
    private ArchivoService archivoService;

    // Configuro un endpoint para subir un archivo, que recibe el archivo y la información del usuario (Principal)
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            // Guardo el archivo utilizando el servicio y devuelvo un mensaje de éxito
            String fileName = archivoService.saveFile(file, principal.getName());
            return ResponseEntity.ok().body("Archivo subido exitosamente: " + fileName);
        } catch (Exception e) {
            // En caso de error, devuelvo un mensaje detallando el problema
            return ResponseEntity.status(500).body("Error al subir archivo: " + e.getMessage());
        }
    }

    // Configuro un endpoint para descargar un archivo especificado por su nombre
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        // Solo permitimos descargar por nombre almacenado bajo storageDir, evitando rutas arbitrarias
        Resource file = archivoService.loadFileFromStorage(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
