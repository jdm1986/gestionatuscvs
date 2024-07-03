package com.example.gestion_curriculums0;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findByNombreContaining(String nombre);

    List<Curriculum> findByApellidoContaining(String apellido);


    @Query("SELECT c FROM Curriculum c JOIN c.etiquetas e WHERE e.nombre LIKE %:etiqueta%")
    List<Curriculum> findByEtiquetasContaining(@Param("etiqueta") String etiqueta);

    @Query("SELECT c FROM Curriculum c JOIN FETCH c.etiquetas WHERE c.id = :id")
    Optional<Curriculum> findByIdWithEtiquetas(@Param("id") Long id);

    @Query("SELECT c FROM Curriculum c WHERE c.nombre LIKE %:nombre% AND c.apellido LIKE %:apellido% AND EXISTS (SELECT e FROM Etiqueta e WHERE e.curriculum.id = c.id AND e.nombre LIKE %:etiqueta%)")
    List<Curriculum> findByNombreAndApellidoAndEtiqueta(@Param("nombre") String nombre, @Param("apellido") String apellido, @Param("etiqueta") String etiqueta);

    List<Curriculum> findByUsuarioId(Long usuarioId);

    @Query("SELECT DISTINCT c FROM Curriculum c JOIN c.etiquetas e WHERE "
            + "(c.nombre LIKE %:nombre% OR :nombre IS NULL) AND "
            + "(c.apellido LIKE %:apellido% OR :apellido IS NULL) AND "
            + "(e.nombre LIKE %:etiqueta% OR :etiqueta IS NULL)")
    Page<Curriculum> findByAdvancedSearch(@Param("nombre") String nombre, @Param("apellido") String apellido, @Param("etiqueta") String etiqueta, Pageable pageable);
}
