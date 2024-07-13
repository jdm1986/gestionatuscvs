// NotaDTO.java
package com.example.gestion_curriculums0.model;

import java.time.LocalDateTime;

public class NotaDTO {
    private Long id;
    private String contenido;
    private LocalDateTime fechaCreacion;

    // Constructor, getters y setters

    public NotaDTO(Long id, String contenido, LocalDateTime fechaCreacion) {
        this.id = id;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
