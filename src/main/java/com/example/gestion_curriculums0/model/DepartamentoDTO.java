// DepartamentoDTO.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.model;

// Defino este DTO (Data Transfer Object) para transferir los datos de departamentos de forma sencilla

public class DepartamentoDTO {
    private Long id;
    private String nombre;
    private Long userId;

    // Getters y setters

    public Long getId() {
        // Obtengo el ID del departamento
        return id;
    }

    public void setId(Long id) {
        // Establezco el ID del departamento
        this.id = id;
    }

    public String getNombre() {
        // Obtengo el nombre del departamento
        return nombre;
    }

    public void setNombre(String nombre) {
        // Establezco el nombre del departamento
        this.nombre = nombre;
    }

    public Long getUserId() {
        // Obtengo el userId asociado al departamento
        return userId;
    }

    public void setUserId(Long userId) {
        // Establezco el userId asociado al departamento
        this.userId = userId;
    }
}
