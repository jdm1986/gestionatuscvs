package com.example.gestion_curriculums0;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/archivos")
@CrossOrigin(origins = "http://localhost:8000")
public class ArchivoController {

    public static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private CustomOpenAiService openAiService;

    @PostMapping("/subir")
    public ResponseEntity<String> subirArchivo(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("sexo") String sexo,
            @RequestParam("telefono") String telefono,
            @RequestParam("email") String email) {
        try {
            // Crear un nuevo curriculum
            Curriculum curriculum = new Curriculum();
            curriculum.setNombre(nombre);
            curriculum.setApellido(apellido);
            curriculum.setSexo(sexo);
            curriculum.setTelefono(telefono);
            curriculum.setEmail(email);

            // Guardar el archivo en el sistema de archivos
            String fileName = archivo.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName).toAbsolutePath().normalize();
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, archivo.getBytes());

            // Imprimir detalles del archivo en los logs
            System.out.println("Archivo subido: " + filePath.toString());

            // Actualizar la ruta del archivo en el curriculum
            curriculum.setPdfPath(fileName);

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

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<StreamingResponseBody> getFile(@PathVariable String fileName) {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
        if (!Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        StreamingResponseBody responseBody = outputStream -> Files.copy(filePath, outputStream);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(responseBody);
    }

    private String loadCvText(String pdfPath) {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el PDF", e);
        }
    }
}
