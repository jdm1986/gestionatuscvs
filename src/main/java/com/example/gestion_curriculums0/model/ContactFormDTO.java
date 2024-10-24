// ContactFormDTO.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.model;

// Defino esta clase como un Data Transfer Object (DTO) para el formulario de contacto
public class ContactFormDTO {
    private String name;
    private String email;
    private String message;

    // Getters y Setters para los campos del formulario de contacto

    public String getName() {
        // Obtengo el nombre ingresado en el formulario
        return name;
    }

    public void setName(String name) {
        // Establezco el nombre en el formulario
        this.name = name;
    }

    public String getEmail() {
        // Obtengo el correo electrónico ingresado en el formulario
        return email;
    }

    public void setEmail(String email) {
        // Establezco el correo electrónico en el formulario
        this.email = email;
    }

    public String getMessage() {
        // Obtengo el mensaje ingresado en el formulario
        return message;
    }

    public void setMessage(String message) {
        // Establezco el mensaje en el formulario
        this.message = message;
    }
}
