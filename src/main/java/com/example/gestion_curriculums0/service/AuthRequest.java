package com.example.gestion_curriculums0.service;

/*
  Clase AuthRequest: Me encargo de representar los datos de autenticación que
  recibo en las solicitudes relacionadas con el inicio de sesión y el registro de usuarios.
  Contiene el nombre de usuario, la contraseña, el email y los roles asociados al usuario.
 */
public class AuthRequest {
    private String username;  // Nombre de usuario para la autenticación
    private String password;  // Contraseña del usuario
    private String email;     // Correo electrónico del usuario (opcional dependiendo del contexto)
    private String roles;     // Roles del usuario, por ejemplo, USER o ADMIN

    // Getters y setters para acceder y modificar los valores

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
