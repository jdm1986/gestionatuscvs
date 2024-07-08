package com.example.gestion_curriculums0.model;

import com.example.gestion_curriculums0.CurriculumDTO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioDTO {
    private Long id;
    private String nombreUsuario;
    private String email;
    private List<String> roles;
    private List<CurriculumDTO> curriculums;

    // Constructor, getters y setters
    public UsuarioDTO() {}

    public UsuarioDTO(Long id, String nombreUsuario, String email, String roles, List<CurriculumDTO> curriculums) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.roles = Arrays.asList(roles.split(",")); // Convertir cadena de roles a lista
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = Arrays.asList(roles.split(",")); // Convertir cadena de roles a lista
    }

    public List<CurriculumDTO> getCurriculums() {
        return curriculums;
    }

    public void setCurriculums(List<CurriculumDTO> curriculums) {
        this.curriculums = curriculums;
    }
}
