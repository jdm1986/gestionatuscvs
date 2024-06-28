package com.example.gestion_curriculums0;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    List<Curriculum> findByNombreContaining(String nombre);

    List<Curriculum> findByApellidoContaining(String apellido);

    @Query("SELECT c FROM Curriculum c JOIN c.etiquetas e WHERE e = :etiqueta")
    List<Curriculum> findByEtiqueta(@Param("etiqueta") String etiqueta);

    @Query("SELECT c FROM Curriculum c WHERE c.usuario.id = :usuarioId")
    List<Curriculum> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
