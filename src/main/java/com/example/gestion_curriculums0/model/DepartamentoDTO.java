// DTO para Departamento
package com.example.gestion_curriculums0.model;

// Defino este DTO (Data Transfer Object) para transferir los datos de departamentos de forma sencilla

public class DepartamentoDTO {
    private Long id;
    private String nombre;
    private Long userId;

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
