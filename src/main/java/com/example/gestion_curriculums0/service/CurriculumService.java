package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.Curriculum;
import com.example.gestion_curriculums0.repository.CurriculumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurriculumService {

    @Autowired
    private CurriculumRepository curriculumRepository;

    public List<Curriculum> getAllCurriculums() {
        return curriculumRepository.findAll();
    }

    public Curriculum getCurriculumById(Long id) {
        return curriculumRepository.findById(id).orElse(null);
    }

    public List<Curriculum> getCurriculumsByUsuarioId(Long usuarioId) {
        return curriculumRepository.findByUsuarioId(usuarioId);
    }

    public Curriculum saveCurriculum(Curriculum curriculum) {
        return curriculumRepository.save(curriculum);
    }

    public void deleteCurriculum(Long id) {
        curriculumRepository.deleteById(id);
    }
}
