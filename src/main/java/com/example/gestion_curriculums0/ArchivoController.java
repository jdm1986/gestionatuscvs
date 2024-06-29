package com.example.gestion_curriculums0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/archivos")
public class ArchivoController {

    private static String UPLOAD_DIR = "uploads/";

    @Autowired
    private CurriculumRepository curriculumRepository;

    @PostMapping("/subir/{id}")
    public ResponseEntity<String> subirArchivo(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        try {
            // Obtener el curriculum
            Curriculum curriculum = curriculumRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Curriculum not found"));

            // Guardar el archivo en el sistema de archivos
            String fileName = archivo.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, archivo.getBytes());

            // Actualizar la ruta del archivo en el curriculum
            curriculum.setPdfPath(filePath.toString());
            curriculumRepository.save(curriculum);

            return ResponseEntity.ok("Archivo subido exitosamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir el archivo");
        }
    }
}
