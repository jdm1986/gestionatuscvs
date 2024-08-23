package com.example.gestion_curriculums0.repository;

import com.example.gestion_curriculums0.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByCurriculumId(Long curriculumId);

    // Agregar el método para eliminar notas por curriculumId
    void deleteByCurriculumId(Long curriculumId);
}
