package com.example.gestion_curriculums0.repository;

import com.example.gestion_curriculums0.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Declaro la interfaz NotaRepository que extiende JpaRepository para manejar las operaciones CRUD de la entidad Nota
public interface NotaRepository extends JpaRepository<Nota, Long> {

    // Defino un método para obtener todas las notas asociadas a un curriculum por su ID
    List<Nota> findByCurriculumId(Long curriculumId);

    // Defino un método para eliminar todas las notas asociadas a un curriculum por su ID
    void deleteByCurriculumId(Long curriculumId);
}
