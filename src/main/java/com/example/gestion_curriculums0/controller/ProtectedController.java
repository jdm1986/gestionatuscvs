package com.example.gestion_curriculums0.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Defino este controlador para manejar un endpoint protegido
@RestController
@RequestMapping("/protected")
public class ProtectedController {

    // Este endpoint es accesible solo si el usuario tiene autorización adecuada
    @GetMapping
    public String getProtected() {
        return "This is a protected endpoint";
    }
}
