/* Esta clase utiliza la librería Tesseract para realizar el reconocimiento óptico de caracteres (OCR)
 en imágenes. Permite extraer texto a partir de archivos de imagen, procesando las imágenes y
 devolviendo el texto contenido en ellas. Es útil para escanear CVs o documentos que no están en formato digital*/

package com.example.gestion_curriculums0.service;

import org.springframework.stereotype.Service;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

@Service
public class OcrService {

    private final Tesseract tesseract;

    // En el constructor inicializo Tesseract y configuro su ruta de datos y el idioma (español en este caso)
    public OcrService() {
        tesseract = new Tesseract();
        tesseract.setDatapath("tessdata"); // Ruta al directorio de los datos de Tesseract
        tesseract.setLanguage("spa"); // Configurar el idioma a español
    }

    // Este método se encarga de extraer el texto de un archivo de imagen usando Tesseract
    public String extractTextFromImage(File file) {
        try {
            return tesseract.doOCR(file); // Realizo el OCR sobre el archivo de imagen proporcionado
        } catch (TesseractException e) {
            throw new RuntimeException("Error al extraer texto de la imagen", e); // Manejo errores en caso de fallos
        }
    }
}
