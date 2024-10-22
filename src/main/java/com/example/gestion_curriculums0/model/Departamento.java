// Defino la entidad Departamento para almacenar los diferentes departamentos
package com.example.gestion_curriculums0.model;

import jakarta.persistence.*;

@Entity
@Table(name = "departamentos")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    // Relación muchos-a-uno con la entidad Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario; // Asocia el departamento con un usuario específico

    // Campo userId para facilitar el acceso, pero sin interferir con la relación
    @Column(name = "user_id", insertable = false, updatable = false)
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getUserId() {
        return userId;
    }

    // No es necesario un setter para userId ya que se asigna a través del usuario
}
