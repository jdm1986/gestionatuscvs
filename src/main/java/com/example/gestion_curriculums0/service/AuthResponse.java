// AuthResponse.java
// Indico el paquete al que pertenece esta clase
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

    // Constructor: Inicializo la respuesta con el token JWT, el nombre de usuario y el ID del usuario
    public AuthResponse(String jwt, String username, Long userId) {
        this.jwt = jwt;
        this.username = username;
        this.userId = userId;
    }

    // Getters y setters para acceder y modificar los valores

    public String getJwt() {
        // Obtengo el token JWT generado
        return jwt;
    }

    public void setJwt(String jwt) {
        // Establezco el token JWT
        this.jwt = jwt;
    }

    public String getUsername() {
        // Obtengo el nombre de usuario autenticado
        return username;
    }

    public void setUsername(String username) {
        // Establezco el nombre de usuario autenticado
        this.username = username;
    }

    public Long getUserId() {
        // Obtengo el ID del usuario autenticado
        return userId;
    }

    public void setUserId(Long userId) {
        // Establezco el ID del usuario autenticado
        this.userId = userId;
    }
}
