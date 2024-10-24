// DatabaseController.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.controller;

// Defino este controlador para manejar operaciones relacionadas con la base de datos

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Anoto la clase como un controlador REST para definir endpoints relacionados con la base de datos
@RestController
public class DatabaseController {

    // Inyecto el JdbcTemplate para interactuar con la base de datos
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Configuro un endpoint para verificar la conexión a la base de datos
    @GetMapping("/check-connection")
    public String checkConnection() {
        try {
            // Ejecuto una consulta simple para comprobar si la conexión es exitosa
            jdbcTemplate.execute("SELECT 1");
            return "Connection successful!";
        } catch (Exception e) {
            // Si ocurre algún error, devuelvo el mensaje indicando el fallo de la conexión
            return "Connection failed: " + e.getMessage();
        }
    }
}
