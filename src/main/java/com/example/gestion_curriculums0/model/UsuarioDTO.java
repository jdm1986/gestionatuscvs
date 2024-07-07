package com.example.gestion_curriculums0.model;

import com.example.gestion_curriculums0.CurriculumDTO;

import java.util.List;

public class UsuarioDTO {
    private Long id;
    private String nombreUsuario;
    private String email;
    private String roles;
    private List<CurriculumDTO> curriculums;

    // Constructor, getters y setters
    public UsuarioDTO() {}

    public UsuarioDTO(Long id, String nombreUsuario, String email, String roles, List<CurriculumDTO> curriculums) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.roles = roles;
        this.curriculums = curriculums;
    }

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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public List<CurriculumDTO> getCurriculums() {
        return curriculums;
    }

    public void setCurriculums(List<CurriculumDTO> curriculums) {
        this.curriculums = curriculums;
    }
}
