package com.example.gestion_curriculums0.model;

import java.time.LocalDateTime;

// Defino este DTO (Data Transfer Object) para transferir los datos del curriculum de forma sencilla
public class CurriculumDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String pdfPath;
    private String resumenCv;
    private String sexo;
    private String telefono;
    private String email;
    private String departamento;
    private LocalDateTime fechaInsercion;

    // Constructor para inicializar los atributos del DTO
    public CurriculumDTO(Long id, String nombre, String apellido, String pdfPath, String cvBruto,
                         String sexo, String telefono, String email, String departamento, LocalDateTime fechaInsercion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.pdfPath = pdfPath;
        this.resumenCv = resumenCv;
        this.sexo = sexo;
        this.telefono = telefono;
        this.email = email;
        this.departamento = departamento;
        this.fechaInsercion = fechaInsercion;
    }

    // Getters y Setters para acceder y modificar los atributos del DTO

    public Long getId() {
        // Obtengo el ID del curriculum
        return id;
    }

    public void setId(Long id) {
        // Establezco el ID del curriculum
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

    public String getResumenCv() {
        // Obtengo el resumen del CV
        return resumenCv;
    }

    public void setResumenCv(String resumenCv) {
        // Establezco el resumen del CV
        this.resumenCv = resumenCv;
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

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public LocalDateTime getFechaInsercion() {
        // Obtengo la fecha de inserción del curriculum
        return fechaInsercion;
    }

    public void setFechaInsercion(LocalDateTime fechaInsercion) {
        // Establezco la fecha de inserción del curriculum
        this.fechaInsercion = fechaInsercion;
    }
}
