package com.example.gestion_curriculums0;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findByNombreContaining(String nombre);

    List<Curriculum> findByApellidoContaining(String apellido);

    @Query("SELECT c FROM Curriculum c JOIN c.etiquetas e WHERE e.nombre LIKE %:etiqueta%")
    List<Curriculum> findByEtiquetasContaining(@Param("etiqueta") String etiqueta);

    List<Curriculum> findByUsuarioId(Long usuarioId);
}
