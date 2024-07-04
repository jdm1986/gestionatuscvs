package com.example.gestion_curriculums0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class IAController {

    private final CurriculumRepository curriculumRepository;
    private final CustomOpenAiService openAiService;

    @Autowired
    public IAController(CurriculumRepository curriculumRepository, CustomOpenAiService openAiService) {
        this.curriculumRepository = curriculumRepository;
        this.openAiService = openAiService;
    }

    @GetMapping("/buscar")
    public List<CurriculumDTO> buscarCandidatos(@RequestParam String query) {
        List<Curriculum> allCurriculums = curriculumRepository.findAll();
        return allCurriculums.stream()
                .filter(curriculum -> curriculum.getResumenCv() != null && curriculum.getResumenCv().contains(query))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private String loadCvText(String pdfPath) {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el PDF", e);
        }
    }

    private CurriculumDTO convertToDTO(Curriculum curriculum) {
        return new CurriculumDTO(curriculum.getId(), curriculum.getNombre(), curriculum.getApellido(), curriculum.getPdfPath(), curriculum.getEtiquetas().stream().map(Etiqueta::getNombre).collect(Collectors.toList()));
    }
}
