package com.example.gestion_curriculums0.service;

/*
  Clase AuthResponse: Me encargo de encapsular la respuesta que envío al usuario
  después de un proceso exitoso de autenticación. Proporciono el JWT generado,
  el nombre de usuario y el ID del usuario.
 */
public class AuthResponse {
    private String jwt;       // El token JWT que genero para el usuario autenticado
    private String username;  // El nombre de usuario que se autenticó correctamente
    private Long userId;      // El ID del usuario autenticado

    //Constructor: Inicializo la respuesta con el token JWT, el nombre de usuario y el ID del usuario.

    public AuthResponse(String jwt, String username, Long userId) {
        this.jwt = jwt;
        this.username = username;
        this.userId = userId;
    }

    // Getters y setters para acceder y modificar los valores

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
