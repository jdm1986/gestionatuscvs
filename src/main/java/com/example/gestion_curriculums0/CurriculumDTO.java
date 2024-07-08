package com.example.gestion_curriculums0;

public class CurriculumDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String pdfPath;
    private String resumenCv;
    private String sexo;
    private String telefono;
    private String email;

    // Constructor
    public CurriculumDTO(Long id, String nombre, String apellido, String pdfPath, String resumenCv, String sexo, String telefono, String email) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.pdfPath = pdfPath;
        this.resumenCv = resumenCv;
        this.sexo = sexo;
        this.telefono = telefono;
        this.email = email;
    }

    // Getters y Setters

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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getResumenCv() {
        return resumenCv;
    }

    public void setResumenCv(String resumenCv) {
        this.resumenCv = resumenCv;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
