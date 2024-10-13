package com.example.gestion_curriculums0.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Defino la entidad Nota que estará relacionada con los curriculums
@Entity
@Table(name = "notas")
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Contenido de la nota, es obligatorio
    @Column(nullable = false)
    private String contenido;

    // Fecha de creación de la nota, es obligatoria y se establece automáticamente al crearse
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // Relación muchos-a-uno con el curriculum, la nota está vinculada a un curriculum
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false)
    private Curriculum curriculum;

    // Antes de que la nota sea persistida, establezco la fecha de creación como la fecha actual
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters

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

    public Curriculum getCurriculum() {
        // Obtengo el curriculum asociado a esta nota
        return curriculum;
    }

    public void setCurriculum(Curriculum curriculum) {
        // Establezco el curriculum al que pertenece esta nota
        this.curriculum = curriculum;
    }
}
