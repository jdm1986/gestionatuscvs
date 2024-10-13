/* Esta clase se encarga de procesar archivos PDF y extraer su contenido en formato de texto.
   Utiliza la librería PDFBox para cargar y leer el contenido de los PDFs.*/

package com.example.gestion_curriculums0.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;

@Service
public class PdfService {

    // Este método toma un archivo PDF como entrada y devuelve su contenido en forma de texto
    public String extractTextFromPdf(File file) throws IOException {
        // Abro el documento PDF, lo leo y extraigo su contenido usando PDFBox
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper(); // PDFTextStripper extrae el texto del documento
            return pdfStripper.getText(document); // Devuelvo el texto extraído
        }
    }
}
