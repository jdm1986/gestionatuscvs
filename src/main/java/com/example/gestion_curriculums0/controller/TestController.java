package com.example.gestion_curriculums0.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Defino este controlador para manejar endpoints de prueba que están protegidos por roles
@RestController
@RequestMapping("/api/test")
public class TestController {

    // Este endpoint solo es accesible para usuarios con el rol de USER
    @GetMapping("/user")
    public ResponseEntity<String> userEndpoint() {
        return ResponseEntity.ok("You have accessed a USER protected endpoint!");
    }

    // Este endpoint solo es accesible para usuarios con el rol de ADMIN
    @GetMapping("/admin")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("You have accessed an ADMIN protected endpoint!");
    }
}
