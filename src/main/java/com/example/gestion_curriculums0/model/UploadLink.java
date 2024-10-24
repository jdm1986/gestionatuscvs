// UploadLink.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.model;

// Defino la entidad UploadLink que me permite crear enlaces únicos para la subida de archivos

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UploadLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Token único que identifica el enlace de subida
    @Column(nullable = false, unique = true)
    private String token;

    // Fecha de caducidad del enlace
    @Column(nullable = false)
    private LocalDateTime expirationDate;

    // Límite máximo de subidas permitidas, lo inicializo con 5 por defecto
    @Column(nullable = false)
    private int uploadLimit = 5;

    // Contador que lleva el registro del número de subidas realizadas
    @Column(nullable = false)
    private int uploadCount = 0;

    // Relación muchos-a-uno con el usuario que generó el enlace
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Getters y Setters para acceder y modificar los atributos

    public Long getId() {
        // Obtengo el ID del enlace de subida
        return id;
    }

    public void setId(Long id) {
        // Establezco el ID del enlace de subida
        this.id = id;
    }

    public String getToken() {
        // Obtengo el token único del enlace de subida
        return token;
    }

    public void setToken(String token) {
        // Establezco el token único del enlace de subida
        this.token = token;
    }

    public LocalDateTime getExpirationDate() {
        // Obtengo la fecha de caducidad del enlace
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        // Establezco la fecha de caducidad del enlace
        this.expirationDate = expirationDate;
    }

    public int getUploadLimit() {
        // Obtengo el límite máximo de subidas permitidas
        return uploadLimit;
    }

    public void setUploadLimit(int uploadLimit) {
        // Establezco el límite máximo de subidas permitidas
        this.uploadLimit = uploadLimit;
    }

    public int getUploadCount() {
        // Obtengo el número de subidas realizadas hasta ahora
        return uploadCount;
    }

    public void setUploadCount(int uploadCount) {
        // Establezco el número de subidas realizadas
        this.uploadCount = uploadCount;
    }

    public Usuario getUsuario() {
        // Obtengo el usuario que generó el enlace
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        // Establezco el usuario que generó el enlace
        this.usuario = usuario;
    }
}
