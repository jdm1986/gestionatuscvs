package com.example.gestion_curriculums0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/archivos")
public class ArchivoController {

    private static String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

    @Autowired
    private CurriculumRepository curriculumRepository;

    @PostMapping("/subir/{id}")
    public String uploadFile(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            return "El archivo está vacío";
        }

        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));

        String fileName = archivo.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR, fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, archivo.getBytes());

        curriculum.setPdfPath(path.toString());
        curriculumRepository.save(curriculum);

        return "Archivo subido correctamente: " + fileName;
    }

    @GetMapping("/descargar/{id}")
    public byte[] downloadFile(@PathVariable Long id) throws IOException {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));

        Path path = Paths.get(curriculum.getPdfPath());
        return Files.readAllBytes(path);
    }
}
