package com.example.gestion_curriculums0.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Defino la entidad UserLog que me permite registrar las acciones realizadas por los usuarios
@Entity
@Table(name = "user_logs")
public class UserLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Almaceno el nombre de usuario que realiza la acción
    private String username;

    // Descripción de la acción realizada por el usuario
    private String action;

    // Almaceno la fecha y hora en la que se registra la acción
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Getters y Setters para acceder y modificar los atributos

    public Long getId() {
        // Obtengo el ID del registro de usuario
        return id;
    }

    public void setId(Long id) {
        // Establezco el ID del registro de usuario
        this.id = id;
    }

    public String getUsername() {
        // Obtengo el nombre de usuario asociado al registro
        return username;
    }

    public void setUsername(String username) {
        // Establezco el nombre de usuario asociado al registro
        this.username = username;
    }

    public String getAction() {
        // Obtengo la acción realizada por el usuario
        return action;
    }

    public void setAction(String action) {
        // Establezco la acción realizada por el usuario
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        // Obtengo la fecha y hora del registro
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        // Establezco la fecha y hora del registro
        this.timestamp = timestamp;
    }
}
