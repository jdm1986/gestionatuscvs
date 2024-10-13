package com.example.gestion_curriculums0.model;

import java.util.List;

// Defino la clase DTO para el usuario, que me permitirá transferir datos entre capas
public class UsuarioDTO {

    private Long id;
    private String nombreUsuario;
    private String email;
    private List<String> roles;
    private List<CurriculumDTO> curriculums;

    // Constructor que utilizo para inicializar todos los atributos de UsuarioDTO
    public UsuarioDTO(Long id, String nombreUsuario, String email, List<String> roles, List<CurriculumDTO> curriculums) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.roles = roles;
        this.curriculums = curriculums;
    }

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

    public String getEmail() {
        // Obtengo el email del usuario
        return email;
    }

    public void setEmail(String email) {
        // Establezco el email del usuario
        this.email = email;
    }

    public List<String> getRoles() {
        // Obtengo la lista de roles del usuario
        return roles;
    }

    public void setRoles(List<String> roles) {
        // Establezco la lista de roles del usuario
        this.roles = roles;
    }

    public List<CurriculumDTO> getCurriculums() {
        // Obtengo la lista de curriculums asociados al usuario
        return curriculums;
    }

    public void setCurriculums(List<CurriculumDTO> curriculums) {
        // Establezco la lista de curriculums asociados al usuario
        this.curriculums = curriculums;
    }
}
