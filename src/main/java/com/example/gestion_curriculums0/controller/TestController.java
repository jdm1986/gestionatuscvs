// TestController.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.controller;

// Defino este controlador para manejar endpoints de prueba que están protegidos por roles

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Anoto la clase como un controlador REST y la asocio a la ruta base "/api/test"
@RestController
@RequestMapping("/api/test")
public class TestController {

    // Configuro un endpoint accesible solo para usuarios con el rol de USER
    @GetMapping("/user")
    public ResponseEntity<String> userEndpoint() {
        // Devuelvo un mensaje indicando que el usuario ha accedido a un endpoint protegido para USER
        return ResponseEntity.ok("You have accessed a USER protected endpoint!");
    }

    // Configuro un endpoint accesible solo para usuarios con el rol de ADMIN
    @GetMapping("/admin")
    public ResponseEntity<String> adminEndpoint() {
        // Devuelvo un mensaje indicando que el usuario ha accedido a un endpoint protegido para ADMIN
        return ResponseEntity.ok("You have accessed an ADMIN protected endpoint!");
    }
}
