package com.example.gestion_curriculums0.repository;

import com.example.gestion_curriculums0.model.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findByNombreContaining(String nombre);

    List<Curriculum> findByApellidoContaining(String apellido);

    List<Curriculum> findByUsuarioId(Long usuarioId);

    @Query("SELECT DISTINCT c FROM Curriculum c WHERE "
            + "(c.nombre LIKE %:nombre% OR :nombre IS NULL) AND "
            + "(c.apellido LIKE %:apellido% OR :apellido IS NULL)")
    Page<Curriculum> findByAdvancedSearch(@Param("nombre") String nombre, @Param("apellido") String apellido, Pageable pageable);
}
