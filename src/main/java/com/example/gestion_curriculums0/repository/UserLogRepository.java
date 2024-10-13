package com.example.gestion_curriculums0.repository;

import com.example.gestion_curriculums0.model.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Declaro la interfaz UserLogRepository que extiende JpaRepository para manejar las operaciones CRUD de la entidad UserLog
@Repository
public interface UserLogRepository extends JpaRepository<UserLog, Long> {
    // No necesito definir métodos adicionales aquí, JpaRepository ya proporciona los métodos básicos como save(), findAll(), delete(), etc.
}
