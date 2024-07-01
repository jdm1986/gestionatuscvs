package com.example.gestion_curriculums0;

import java.util.List;

public class CurriculumDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String pdfPath;
    private List<String> etiquetas;

    public CurriculumDTO(Long id, String nombre, String apellido, String pdfPath, List<String> etiquetas) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.pdfPath = pdfPath;
        this.etiquetas = etiquetas;
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

    public List<String> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(List<String> etiquetas) {
        this.etiquetas = etiquetas;
    }
}
