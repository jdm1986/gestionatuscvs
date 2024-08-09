package com.example.gestion_curriculums0.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:8000")
public class AdminController {

    // Endpoint protegido para ver los registros de usuarios
    @GetMapping("/registro-usuarios")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> viewUserRegistrations() {
        String logFilePath = "registro_usuarios.txt";

        try {
            String logs = new String(Files.readAllBytes(Paths.get(logFilePath)));
            return ResponseEntity.ok(logs);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al leer el archivo de registros: " + e.getMessage());
        }
    }

    // Otros endpoints de tu AdminController permanecen sin cambios
}
