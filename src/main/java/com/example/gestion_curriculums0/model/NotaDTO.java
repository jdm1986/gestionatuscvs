// NotaDTO.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.model;

// Defino este DTO (Data Transfer Object) para transferir los datos de la entidad Nota

import java.time.LocalDateTime;

public class NotaDTO {
    private Long id;
    private String contenido;
    private LocalDateTime fechaCreacion;

    // Constructor que inicializa los atributos del DTO
    public NotaDTO(Long id, String contenido, LocalDateTime fechaCreacion) {
        this.id = id;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y setters para acceder y modificar los atributos del DTO

    public Long getId() {
        // Obtengo el ID de la nota
        return id;
    }

    public void setId(Long id) {
        // Establezco el ID de la nota
        this.id = id;
    }

    public String getContenido() {
        // Obtengo el contenido de la nota
        return contenido;
    }

    public void setContenido(String contenido) {
        // Establezco el contenido de la nota
        this.contenido = contenido;
    }

    public LocalDateTime getFechaCreacion() {
        // Obtengo la fecha de creación de la nota
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        // Establezco la fecha de creación de la nota
        this.fechaCreacion = fechaCreacion;
    }
}
