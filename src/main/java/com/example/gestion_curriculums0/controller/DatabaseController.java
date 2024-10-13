package com.example.gestion_curriculums0.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Defino este controlador para manejar operaciones relacionadas con la base de datos
@RestController
public class DatabaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Endpoint para verificar la conexión a la base de datos
    @GetMapping("/check-connection")
    public String checkConnection() {
        try {
            // Ejecuto una simple consulta para comprobar si la conexión es exitosa
            jdbcTemplate.execute("SELECT 1");
            return "Connection successful!";
        } catch (Exception e) {
            // En caso de error, devuelvo el mensaje de fallo de conexión
            return "Connection failed: " + e.getMessage();
        }
    }
}
