package com.example.gestion_curriculums0.service;

import org.springframework.stereotype.Service;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

@Service
public class OcrService {

    private final Tesseract tesseract;

    public OcrService() {
        tesseract = new Tesseract();
        tesseract.setDatapath("tessdata"); // Ruta al directorio de los datos de Tesseract
        tesseract.setLanguage("spa"); // Configurar el idioma a español
    }

    public String extractTextFromImage(File file) {
        try {
            return tesseract.doOCR(file);
        } catch (TesseractException e) {
            throw new RuntimeException("Error al extraer texto de la imagen", e);
        }
    }
}
