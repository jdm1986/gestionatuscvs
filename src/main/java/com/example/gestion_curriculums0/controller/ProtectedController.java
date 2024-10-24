// ProtectedController.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.controller;

// Defino este controlador para manejar un endpoint protegido

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Anoto la clase como un controlador REST y la asocio a la ruta base "/protected"
@RestController
@RequestMapping("/protected")
public class ProtectedController {

    // Configuro un endpoint accesible solo si el usuario tiene la autorización adecuada
    @GetMapping
    public String getProtected() {
        // Devuelvo un mensaje indicando que este es un endpoint protegido
        return "This is a protected endpoint";
    }
}
