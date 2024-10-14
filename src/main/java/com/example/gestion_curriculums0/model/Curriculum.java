package com.example.gestion_curriculums0.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

// Defino la entidad Curriculum que representa el currículum de un usuario
@Entity
@Table(name = "curriculums")
public class Curriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del usuario, no puede ser nulo
    @Column(nullable = false)
    private String nombre;

    // Apellido del usuario, no puede ser nulo
    @Column(nullable = false)
    private String apellido;

    // Ruta del archivo PDF que contiene el currículum, no puede ser nulo
    @Column(name = "pdf_path", nullable = false)
    private String pdfPath;

    // Resumen del CV con una longitud máxima de 2000 caracteres (Descartado USO DE IA para resumir los CV).
    @Column(name = "resumen_cv", length = 2000)
    private String resumenCv;

    // Texto extraído del CV, con un límite de 4000 caracteres
    @Column(name = "cv_bruto", length = 4000)
    private String cvBruto;

    // Sexo del usuario, campo obligatorio
    @Column(nullable = false)
    private String sexo;

    // Teléfono del usuario, campo obligatorio
    @Column(nullable = false)
    private String telefono;

    // Correo electrónico del usuario, campo obligatorio
    @Column(nullable = false)
    private String email;

    // Fecha de inserción, se establece automáticamente cuando se crea el curriculum
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaInsercion;

    // Nuevo campo para almacenar el departamento
    @Column(nullable = false)
    private String departamento;

    // Establezco la fecha de inserción justo antes de que el objeto sea persistido en la base de datos
    @PrePersist
    protected void onCreate() {
        this.fechaInsercion = LocalDateTime.now();
    }

    // Relación muchos-a-uno con la entidad Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Relación uno-a-muchos con la entidad Nota, las notas se eliminan si se elimina el curriculum
    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Nota> notas;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        // Obtengo el nombre del usuario
        return nombre;
    }

    public void setNombre(String nombre) {
        // Establezco el nombre del usuario
        this.nombre = nombre;
    }

    public String getApellido() {
        // Obtengo el apellido del usuario
        return apellido;
    }

    public void setApellido(String apellido) {
        // Establezco el apellido del usuario
        this.apellido = apellido;
    }

    public String getPdfPath() {
        // Obtengo la ruta del archivo PDF del curriculum
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        // Establezco la ruta del archivo PDF del curriculum
        this.pdfPath = pdfPath;
    }
    //Descarto uso de ia para resumir C.V
    public String getResumenCv() {
        // Obtengo el resumen del CV
        return resumenCv;
    }
    //Descarto uso de ia para resumir C.V
    public void setResumenCv(String resumenCv) {
        // Establezco el resumen del CV
        this.resumenCv = resumenCv;
    }

    public String getCvBruto() {
        // Obtengo el texto extraído del CV
        return cvBruto;
    }

    public void setCvBruto(String cvBruto) {
        // Establezco el texto extraído del CV
        this.cvBruto = cvBruto;
    }

    public String getSexo() {
        // Obtengo el sexo del usuario
        return sexo;
    }

    public void setSexo(String sexo) {
        // Establezco el sexo del usuario
        this.sexo = sexo;
    }

    public String getTelefono() {
        // Obtengo el teléfono del usuario
        return telefono;
    }

    public void setTelefono(String telefono) {
        // Establezco el teléfono del usuario
        this.telefono = telefono;
    }

    public String getEmail() {
        // Obtengo el email del usuario
        return email;
    }

    public void setEmail(String email) {
        // Establezco el email del usuario
        this.email = email;
    }

    public LocalDateTime getFechaInsercion() {
        // Obtengo la fecha en la que se insertó el curriculum
        return fechaInsercion;
    }

    public void setFechaInsercion(LocalDateTime fechaInsercion) {
        // Establezco manualmente la fecha de inserción, si es necesario
        this.fechaInsercion = fechaInsercion;
    }

    public String getDepartamento() {
        // Obtengo departamento
        return departamento;
    }

    public void setDepartamento(String departamento) {
        // Establezco departamento manualmente si es necesario
        this.departamento = departamento;
    }

    public Usuario getUsuario() {
        // Obtengo el usuario asociado al curriculum
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        // Establezco el usuario asociado al curriculum
        this.usuario = usuario;
    }

    public List<Nota> getNotas() {
        // Obtengo la lista de notas asociadas al curriculum
        return notas;
    }

    public void setNotas(List<Nota> notas) {
        // Establezco la lista de notas asociadas al curriculum
        this.notas = notas;
    }
}
