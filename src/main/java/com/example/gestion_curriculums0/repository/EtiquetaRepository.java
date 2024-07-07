package com.example.gestion_curriculums0.repository;

import com.example.gestion_curriculums0.model.Etiqueta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {
    Optional<Etiqueta> findByNombre(String nombre);
}
