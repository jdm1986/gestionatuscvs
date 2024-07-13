// NotaService.java
package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.Nota;
import com.example.gestion_curriculums0.repository.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotaService {

    @Autowired
    private NotaRepository notaRepository;

    public List<Nota> getNotasByCurriculumId(Long curriculumId) {
        return notaRepository.findByCurriculumId(curriculumId);
    }

    public Nota saveNota(Nota nota) {
        return notaRepository.save(nota);
    }

    public void deleteNota(Long id) {
        notaRepository.deleteById(id);
    }
}
