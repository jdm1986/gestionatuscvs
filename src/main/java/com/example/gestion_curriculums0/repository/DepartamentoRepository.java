package com.example.gestion_curriculums0.repository;

import com.example.gestion_curriculums0.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    // Método para buscar un departamento por su nombre
    Optional<Departamento> findByNombre(String nombre);
}
