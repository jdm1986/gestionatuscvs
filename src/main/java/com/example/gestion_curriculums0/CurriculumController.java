package com.example.gestion_curriculums0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curriculums")
public class CurriculumController {

    @Autowired
    private CurriculumRepository curriculumRepository;

    @PostMapping
    public Curriculum createCurriculum(@RequestBody Curriculum curriculum) {
        return curriculumRepository.save(curriculum);
    }

    @GetMapping
    public List<Curriculum> getAllCurriculums() {
        return curriculumRepository.findAll();
    }

    @GetMapping("/{id}")
    public Curriculum getCurriculumById(@PathVariable Long id) {
        return curriculumRepository.findById(id).orElseThrow(() -> new RuntimeException("Curriculum not found"));
    }

    @PutMapping("/{id}")
    public Curriculum updateCurriculum(@PathVariable Long id, @RequestBody Curriculum updatedCurriculum) {
        Curriculum curriculum = curriculumRepository.findById(id).orElseThrow(() -> new RuntimeException("Curriculum not found"));
        curriculum.setNombre(updatedCurriculum.getNombre());
        curriculum.setApellido(updatedCurriculum.getApellido());
        curriculum.setEtiquetas(updatedCurriculum.getEtiquetas());
        curriculum.setPdfPath(updatedCurriculum.getPdfPath());
        return curriculumRepository.save(curriculum);
    }

    @DeleteMapping("/{id}")
    public void deleteCurriculum(@PathVariable Long id) {
        curriculumRepository.deleteById(id);
    }
}
