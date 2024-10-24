// Usuario.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.model;

// Defino la entidad Usuario para almacenar los datos de los usuarios registrados

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Almaceno la fecha de registro del usuario
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaRegistro;

    // Nombre de usuario, debe ser único
    @Column(name = "nombre_usuario", nullable = false, unique = true)
    private String nombreUsuario;

    // Contraseña del usuario, no puede ser nula
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    // Email del usuario, debe ser único
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Almaceno los roles como una cadena separada por comas
    @Column(name = "roles", nullable = false)
    private String roles;

    // Relación uno-a-muchos con los curriculums, un usuario puede tener varios curriculums
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Curriculum> curriculums;

    // Relación uno-a-muchos con la entidad Departamento, un usuario puede tener varios departamentos
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Departamento> departamentos;

    // Token para restablecimiento de contraseña
    @Column(name = "reset_token")
    private String resetToken;

    // Fecha en la que se generó el token de restablecimiento
    @Column(name = "fecha_token_generado")
    private LocalDateTime fechaTokenGenerado;

    // Getters y Setters para acceder y modificar los atributos

    public Long getId() {
        // Obtengo el ID del usuario
        return id;
    }

    public void setId(Long id) {
        // Establezco el ID del usuario
        this.id = id;
    }

    public String getNombreUsuario() {
        // Obtengo el nombre de usuario
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        // Establezco el nombre de usuario
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        // Obtengo la contraseña del usuario
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        // Establezco la contraseña del usuario
        this.contrasena = contrasena;
    }

    public String getEmail() {
        // Obtengo el email del usuario
        return email;
    }

    public void setEmail(String email) {
        // Establezco el email del usuario
        this.email = email;
    }

    public String getRoles() {
        // Obtengo los roles del usuario
        return roles;
    }

    public void setRoles(String roles) {
        // Establezco los roles del usuario
        this.roles = roles;
    }

    public List<Curriculum> getCurriculums() {
        // Obtengo la lista de curriculums asociados al usuario
        return curriculums;
    }

    public void setCurriculums(List<Curriculum> curriculums) {
        // Establezco la lista de curriculums asociados al usuario
        this.curriculums = curriculums;
    }

    public String getResetToken() {
        // Obtengo el token de restablecimiento de contraseña
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        // Establezco el token de restablecimiento de contraseña
        this.resetToken = resetToken;
    }

    public Date getFechaRegistro() {
        // Obtengo la fecha de registro del usuario
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        // Establezco la fecha de registro del usuario
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaTokenGenerado() {
        // Obtengo la fecha en la que se generó el token de restablecimiento
        return fechaTokenGenerado;
    }

    public void setFechaTokenGenerado(LocalDateTime fechaTokenGenerado) {
        // Establezco la fecha en la que se generó el token de restablecimiento
        this.fechaTokenGenerado = fechaTokenGenerado;
    }

    // Antes de persistir el usuario, establezco la fecha de registro si no está ya definida
    @PrePersist
    protected void onCreate() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = new Date();
        }
    }
}
