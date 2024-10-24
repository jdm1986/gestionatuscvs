// UserLogRepository.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.repository;

// Declaro la interfaz UserLogRepository que extiende JpaRepository para manejar las operaciones CRUD de la entidad UserLog

import com.example.gestion_curriculums0.model.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Anoto la interfaz con @Repository para indicar que es un componente de acceso a datos
@Repository
public interface UserLogRepository extends JpaRepository<UserLog, Long> {
    // No necesito definir métodos adicionales aquí, JpaRepository ya proporciona los métodos básicos como save(), findAll(), delete(), etc.
}
