package com.example.gestion_curriculums0;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class IAController {

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private CustomOpenAiService openAiService;

    @GetMapping("/buscar")
    public List<Curriculum> buscarCandidatos(@RequestParam String query) {
        List<Curriculum> allCurriculums = curriculumRepository.findAll();
        return allCurriculums.stream()
                .filter(curriculum -> openAiService.getCvSummary(loadCvText(curriculum.getPdfPath())).contains(query))
                .collect(Collectors.toList());
    }

    private String loadCvText(String pdfPath) {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el PDF", e);
        }
    }
}
