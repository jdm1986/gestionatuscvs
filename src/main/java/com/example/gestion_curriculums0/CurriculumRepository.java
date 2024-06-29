package com.example.gestion_curriculums0;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findByNombreContaining(String nombre);

    List<Curriculum> findByApellidoContaining(String apellido);

    List<Curriculum> findByEtiquetasContaining(String etiqueta);

    List<Curriculum> findByUsuarioId(Long usuarioId);
}
