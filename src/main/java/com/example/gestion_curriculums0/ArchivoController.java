package com.example.gestion_curriculums0;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private static String UPLOAD_DIR = "uploads/";

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private CustomOpenAiServiceSimulado openAiService; // Usar el servicio simulado

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

            // Procesar y resumir el CV
            String cvText = loadCvText(filePath.toString());
            String resumenCv = openAiService.getCvSummary(cvText);
            curriculum.setResumenCv(resumenCv);

            curriculumRepository.save(curriculum);

            return ResponseEntity.ok("Archivo subido y procesado exitosamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir el archivo");
        }
    }

    private String loadCvText(String pdfPath) {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el PDF", e);
        }
    }
}
