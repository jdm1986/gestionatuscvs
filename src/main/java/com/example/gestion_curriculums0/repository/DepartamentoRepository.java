// DepartamentoRepository.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.repository;

import com.example.gestion_curriculums0.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Anoto la interfaz con @Repository para indicar que es un componente de acceso a datos
@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    // Defino un método para buscar departamentos por userId
    List<Departamento> findByUserId(Long userId);

    // Defino un método para buscar un departamento por su nombre y userId
    Optional<Departamento> findByNombreAndUserId(String nombre, Long userId);
}
