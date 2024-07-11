package com.example.gestion_curriculums0.repository;

import com.example.gestion_curriculums0.model.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findByNombreContaining(String nombre);
    List<Curriculum> findByApellidoContaining(String apellido);
    List<Curriculum> findByUsuarioId(Long usuarioId);
    List<Curriculum> findByCvBrutoContainingAndUsuarioId(String clave, Long usuarioId); // Ajuste aquí
}

