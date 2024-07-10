package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.Curriculum;
import com.example.gestion_curriculums0.repository.CurriculumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CurriculumService {

    @Autowired
    private CurriculumRepository curriculumRepository;

    public List<Curriculum> getAllCurriculums() {
        return curriculumRepository.findAll();
    }

    public Optional<Curriculum> getCurriculumById(Long id) {
        return curriculumRepository.findById(id);
    }

    public List<Curriculum> getCurriculumsByUsuarioId(Long usuarioId) {
        return curriculumRepository.findByUsuarioId(usuarioId);
    }

    public List<Curriculum> buscarPorNombre(String nombre) {
        return curriculumRepository.findByNombreContaining(nombre);
    }

    public List<Curriculum> buscarPorApellido(String apellido) {
        return curriculumRepository.findByApellidoContaining(apellido);
    }

    public List<Curriculum> buscarPorClave(String clave) {
        return curriculumRepository.findByCvBrutoContaining(clave);
    }

    public Curriculum saveCurriculum(Curriculum curriculum) {
        return curriculumRepository.save(curriculum);
    }

    public void deleteCurriculum(Long id) {
        curriculumRepository.deleteById(id);
    }
}
