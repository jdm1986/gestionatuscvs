package com.example.gestion_curriculums0.model;

import java.util.List;

public class UsuarioDTO {

    private Long id;
    private String nombreUsuario;
    private String email;
    private List<String> roles;
    private List<CurriculumDTO> curriculums;

    // Constructor
    public UsuarioDTO(Long id, String nombreUsuario, String email, List<String> roles, List<CurriculumDTO> curriculums) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.roles = roles;
        this.curriculums = curriculums;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<CurriculumDTO> getCurriculums() {
        return curriculums;
    }

    public void setCurriculums(List<CurriculumDTO> curriculums) {
        this.curriculums = curriculums;
    }
}
