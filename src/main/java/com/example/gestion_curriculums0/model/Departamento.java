// Defino la entidad Departamento para almacenar los diferentes departamentos
package com.example.gestion_curriculums0.model;

import jakarta.persistence.*;

@Entity
@Table(name = "departamentos")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Id único del departamento

    @Column(unique = true, nullable = false)
    private String nombre;

    // Relación con el usuario (agregar referencia a userId)
    @Column(nullable = false)
    private Long userId; // Usuario al que pertenece el departamento

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
