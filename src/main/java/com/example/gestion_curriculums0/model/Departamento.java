// Departamento.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.model;

// Defino la entidad Departamento para almacenar los diferentes departamentos

import jakarta.persistence.*;

@Entity
@Table(name = "departamentos")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Defino el nombre del departamento, no puede ser nulo
    @Column(nullable = false)
    private String nombre;

    // Relación muchos-a-uno con la entidad Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario; // Asocia el departamento con un usuario específico

    // Campo userId para facilitar el acceso, pero sin interferir con la relación JPA
    @Column(name = "user_id", insertable = false, updatable = false)
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

    public Usuario getUsuario() {
        // Obtengo el usuario asociado al departamento
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        // Establezco el usuario asociado al departamento
        this.usuario = usuario;
    }

    public Long getUserId() {
        // Obtengo el userId asociado al departamento
        return userId;
    }

    // No es necesario un setter para userId ya que se asigna automáticamente a través de la relación con el usuario
}
