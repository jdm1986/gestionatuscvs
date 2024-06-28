package com.example.gestion_curriculums0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curriculums")
public class CurriculumController {

    @Autowired
    private CurriculumRepository curriculumRepository;

    @GetMapping
    public List<Curriculum> getAllCurriculums() {
        return curriculumRepository.findAll();
    }

    @GetMapping("/{id}")
    public Curriculum getCurriculumById(@PathVariable Long id) {
        return curriculumRepository.findById(id).orElseThrow(() -> new RuntimeException("Curriculum not found"));
    }

    @PostMapping
    public Curriculum createCurriculum(@RequestBody Curriculum curriculum) {
        return curriculumRepository.save(curriculum);
    }

    @PutMapping("/{id}")
    public Curriculum updateCurriculum(@PathVariable Long id, @RequestBody Curriculum updatedCurriculum) {
        Curriculum curriculum = curriculumRepository.findById(id).orElseThrow(() -> new RuntimeException("Curriculum not found"));
        curriculum.setNombre(updatedCurriculum.getNombre());
        curriculum.setApellido(updatedCurriculum.getApellido());
        curriculum.setPdfPath(updatedCurriculum.getPdfPath());
        curriculum.setEtiquetas(updatedCurriculum.getEtiquetas());
        return curriculumRepository.save(curriculum);
    }

    @DeleteMapping("/{id}")
    public void deleteCurriculum(@PathVariable Long id) {
        Curriculum curriculum = curriculumRepository.findById(id).orElseThrow(() -> new RuntimeException("Curriculum not found"));
        curriculumRepository.delete(curriculum);
    }
}
